package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.external.domains.Subscriber
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberLookupRequest
import org.beckn.one.sandbox.bap.client.factories.CartFactory
import org.beckn.one.sandbox.bap.common.City
import org.beckn.one.sandbox.bap.common.Country
import org.beckn.one.sandbox.bap.common.Domain
import org.beckn.one.sandbox.bap.common.factories.ResponseFactory
import org.beckn.one.sandbox.bap.common.factories.SubscriberDtoFactory
import org.beckn.one.sandbox.bap.message.entities.MessageDao
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.schemas.*
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.litote.kmongo.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class CartControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val contextFactory: ContextFactory,
  val uuidFactory: UuidFactory,
  val messageRepository: GenericRepository<MessageDao>
) : DescribeSpec() {

  init {
    describe("Save Cart") {
      val registryBppLookupApi = WireMockServer(4010)
      registryBppLookupApi.start()
      val bppApi = WireMockServer(4011)
      bppApi.start()
      val anotherBppApi = WireMockServer(4012)
      anotherBppApi.start()
      val cart = CartFactory.create(bpp1Uri = bppApi.baseUrl())

      beforeEach {
        bppApi.resetAll()
        registryBppLookupApi.resetAll()
        stubBppLookupApi(registryBppLookupApi, bppApi)
        stubBppLookupApi(registryBppLookupApi, anotherBppApi)
      }

      it("should return error when bpp select call fails") {
        bppApi.stubFor(post("/select").willReturn(serverError()))

        val saveCartResponseString = invokeCartCreateOrUpdateApi(cart)
          .andExpect(status().isInternalServerError)
          .andReturn()
          .response.contentAsString

        val saveCartResponse =
          verifyResponseMessage(saveCartResponseString, cart, ResponseMessage.nack(), BppError.Internal.error())
        verifyThatMessageWasNotPersisted(saveCartResponse)
        verifyThatBppSelectApiWasInvoked(saveCartResponse, cart, bppApi)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, bppApi)

      }

      it("should invoke provide select api and save message") {
        bppApi
          .stubFor(
            post("/select").willReturn(
              okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory)))
            )
          )

        val saveCartResponseString = invokeCartCreateOrUpdateApi(cart)
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val saveCartResponse = verifyResponseMessage(saveCartResponseString, cart, ResponseMessage.ack())
        verifyThatMessageForSelectRequestIsPersisted(saveCartResponse)
        verifyThatBppSelectApiWasInvoked(saveCartResponse, cart, bppApi)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, bppApi)
      }

      it("should validate that cart contains items from only one bpp") {

        val cartWithMultipleBppItems =
          CartFactory.create(bpp1Uri = bppApi.baseUrl(), bpp2Uri = anotherBppApi.baseUrl())

        val saveCartResponseString = invokeCartCreateOrUpdateApi(cartWithMultipleBppItems)
          .andExpect(status().is4xxClientError)
          .andReturn()
          .response.contentAsString

        val saveCartResponse =
          verifyResponseMessage(
            saveCartResponseString,
            cartWithMultipleBppItems,
            ResponseMessage.nack(),
            ProtocolError("BAP_014", "More than one BPP's item(s) selected/initialized")
          )
        verifyThatMessageWasNotPersisted(saveCartResponse)
        verifyThatBppSelectApiWasNotInvoked(bppApi)
        verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi)
        verifyThatSubscriberLookupApiWasNotInvoked(anotherBppApi)
      }

      it("should validate that cart contains items from only one provider") {
        val cartWithMultipleProviderItems =
          CartFactory.create(
            bpp1Uri = bppApi.baseUrl(),
            provider2Id = "padma coffee works",
            provider2Location = listOf("padma coffee works location 1")
          )

        val saveCartResponseString = invokeCartCreateOrUpdateApi(cartWithMultipleProviderItems)
          .andExpect(status().is4xxClientError)
          .andReturn()
          .response.contentAsString

        val saveCartResponse =
          verifyResponseMessage(
            saveCartResponseString,
            cartWithMultipleProviderItems,
            ResponseMessage.nack(),
            ProtocolError("BAP_010", "More than one Provider's item(s) selected/initialized")
          )
        verifyThatMessageWasNotPersisted(saveCartResponse)
        verifyThatBppSelectApiWasNotInvoked(bppApi)
      }

      registryBppLookupApi.stop() //todo: this and any other mocks used have to be cleaned between different tests
    }
  }

  private fun stubBppLookupApi(
    registryBppLookupApi: WireMockServer,
    bppApi: WireMockServer
  ) {
    registryBppLookupApi
      .stubFor(
        post("/lookup")
          .withRequestBody(matchingJsonPath("$.subscriber_id", equalTo(bppApi.baseUrl())))
          .willReturn(okJson(getSubscriberForBpp(bppApi)))
      )
  }

  private fun verifyThatSubscriberLookupApiWasInvoked(
    registryBppLookupApi: WireMockServer,
    bppApi: WireMockServer
  ) {
    registryBppLookupApi.verify(
      postRequestedFor(urlEqualTo("/lookup"))
        .withRequestBody(
          equalToJson(
            objectMapper.writeValueAsString(
              SubscriberLookupRequest(
                subscriber_id = bppApi.baseUrl(),
                type = Subscriber.Type.BPP,
                domain = Domain.LocalRetail.value,
                country = Country.India.value,
                city = City.Bengaluru.value
              )
            )
          )
        )
    )
  }

  private fun verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi: WireMockServer) =
    registryBppLookupApi.verify(0, postRequestedFor(urlEqualTo("/lookup")))

  private fun getSubscriberForBpp(bppApi: WireMockServer) =
    objectMapper.writeValueAsString(
      listOf(
        SubscriberDtoFactory.getDefault(
          subscriber_id = bppApi.baseUrl(),
          baseUrl = bppApi.baseUrl(),
          type = SubscriberDto.Type.BPP,
        )
      )
    )

  private fun verifyThatMessageWasNotPersisted(saveCartResponse: ProtocolAckResponse) {
    val savedMessage = messageRepository.findOne(MessageDao::id eq saveCartResponse.context?.messageId)
    savedMessage shouldBe null
  }

  private fun verifyResponseMessage(
    saveCartResponseString: String,
    cart: CartDto,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null,
  ): ProtocolAckResponse {
    val saveCartResponse = objectMapper.readValue(saveCartResponseString, ProtocolAckResponse::class.java)
    saveCartResponse.context shouldNotBe null
    saveCartResponse.context?.messageId shouldNotBe null
    saveCartResponse.context?.transactionId shouldBe cart.transactionId
    saveCartResponse.context?.action shouldBe ProtocolContext.Action.SELECT
    saveCartResponse.message shouldBe expectedMessage
    saveCartResponse.error shouldBe expectedError
    return saveCartResponse
  }

  private fun verifyThatMessageForSelectRequestIsPersisted(saveCartResponse: ProtocolAckResponse) {
    val savedMessage = messageRepository.findOne(MessageDao::id eq saveCartResponse.context?.messageId)
    savedMessage shouldNotBe null
    savedMessage?.id shouldBe saveCartResponse.context?.messageId
    savedMessage?.type shouldBe MessageDao.Type.Select
  }

  private fun verifyThatBppSelectApiWasInvoked(
    saveCartResponse: ProtocolAckResponse,
    cart: CartDto,
    bppApi: WireMockServer
  ) {
    val protocolSelectRequest = getProtocolSelectRequest(saveCartResponse, cart)
    bppApi.verify(
      postRequestedFor(urlEqualTo("/select"))
        .withRequestBody(equalToJson(objectMapper.writeValueAsString(protocolSelectRequest)))
    )
  }

  private fun verifyThatBppSelectApiWasNotInvoked(bppApi: WireMockServer) =
    bppApi.verify(0, postRequestedFor(urlEqualTo("/select")))

  private fun getProtocolSelectRequest(saveCartResponse: ProtocolAckResponse, cart: CartDto): ProtocolSelectRequest {
    val locations = cart.items?.first()?.provider?.locations?.map { ProtocolLocation(id = it) }
    return ProtocolSelectRequest(
      context = saveCartResponse.context!!,
      message = ProtocolSelectRequestMessage(
        selected = ProtocolSelectMessageSelected(
          provider = ProtocolProvider(
            id = cart.items?.first()?.provider?.id,
            locations = locations
          ),
          items = cart.items?.map {
            ProtocolSelectedItem(
              id = it.id,
              quantity =
              ProtocolItemQuantityAllocated(
                count = it.quantity.count, measure = it.quantity.measure
              ),
            )
          },
        )
      ),
    )
  }

  private fun invokeCartCreateOrUpdateApi(cart: CartDto) = mockMvc
    .perform(
      put("/client/v1/cart")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(cart))
    )

}
