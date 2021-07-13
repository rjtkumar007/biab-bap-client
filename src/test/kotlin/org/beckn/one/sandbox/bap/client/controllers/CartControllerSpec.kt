package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.factories.CartFactory
import org.beckn.one.sandbox.bap.common.factories.ResponseFactory
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
      val providerApi = WireMockServer(4010)
      providerApi.start()
      val cart = CartFactory.create(null, bppUri = providerApi.baseUrl())

      beforeEach {
        providerApi.resetAll()
      }

      it("should return error when bpp select call fails") {
        providerApi.stubFor(post("/select").willReturn(serverError()))

        val saveCartResponseString = invokeCartCreateOrUpdateApi(cart)
          .andExpect(status().isInternalServerError)
          .andReturn()
          .response.contentAsString

        val saveCartResponse =
          verifyResponseMessage(saveCartResponseString, cart, ResponseMessage.nack(), BppError.Internal.error())
        verifyThatMessageWasNotPersisted(saveCartResponse)
        verifyThatBppSelectApiWasInvoked(saveCartResponse, cart, providerApi)
      }

      it("should invoke provide select api and save message") {
        providerApi
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
        verifyThatBppSelectApiWasInvoked(saveCartResponse, cart, providerApi)
      }

      it("should update cart if it already exists") {
        providerApi
          .stubFor(
            post("/select").willReturn(
              okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory)))
            )
          )

        val existingCart = cart.copy(id = "cart id 1")
        val saveCartResponseString = invokeCartCreateOrUpdateApi(existingCart)
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val saveCartResponse = verifyResponseMessage(saveCartResponseString, existingCart, ResponseMessage.ack())
        verifyThatMessageForSelectRequestIsPersisted(saveCartResponse)
        verifyThatBppSelectApiWasInvoked(saveCartResponse, existingCart, providerApi)
      }

      it("should validate that cart contains items from only one bpp and provider") {
        val cartWithMultipleProviderItems =
          CartFactory.createWithMultipleProviders(null, bppUri = providerApi.baseUrl())

        val saveCartResponseString = invokeCartCreateOrUpdateApi(cartWithMultipleProviderItems)
          .andExpect(status().is4xxClientError)
          .andReturn()
          .response.contentAsString

        val saveCartResponse =
          verifyResponseMessage(
            saveCartResponseString,
            cartWithMultipleProviderItems,
            ResponseMessage.nack(),
            ProtocolError("BAP_010", "More than one Provider's item(s) selected")
          )
        verifyThatMessageWasNotPersisted(saveCartResponse)
        verifyThatBppSelectApiWasNotInvoked(saveCartResponse, cartWithMultipleProviderItems, providerApi)
      }
    }
  }

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
    providerApi: WireMockServer
  ) {
    val protocolSelectRequest = getProtocolSelectRequest(saveCartResponse, cart)
    providerApi.verify(
      postRequestedFor(urlEqualTo("/select"))
        .withRequestBody(equalToJson(objectMapper.writeValueAsString(protocolSelectRequest)))
    )
  }

  private fun verifyThatBppSelectApiWasNotInvoked(
    saveCartResponse: ProtocolAckResponse,
    cart: CartDto,
    providerApi: WireMockServer
  ) {
    val protocolSelectRequest = getProtocolSelectRequest(saveCartResponse, cart)
    providerApi.verify(
      0,
      postRequestedFor(urlEqualTo("/select"))
        .withRequestBody(equalToJson(objectMapper.writeValueAsString(protocolSelectRequest)))
    )
  }

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
              descriptor = it.descriptor,
              quantity =
              ProtocolItemQuantityAllocated(
                count = it.quantity.count, measure = it.quantity.measure
              ),
              price = it.price
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
