package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.dtos.OrderDto
import org.beckn.one.sandbox.bap.client.external.domains.Subscriber
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberLookupRequest
import org.beckn.one.sandbox.bap.client.factories.OrderDtoFactory
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
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class InitControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val contextFactory: ContextFactory,
  val uuidFactory: UuidFactory,
  val messageRepository: GenericRepository<MessageDao>
) : DescribeSpec() {
  init {
    describe("Initialize order with BPP") {
      val registryBppLookupApi = WireMockServer(4010)
      registryBppLookupApi.start()
      val providerApi = WireMockServer(4013)
      providerApi.start()
      val provider2Api = WireMockServer(4014)
      provider2Api.start()
      val order = OrderDtoFactory.create(bpp1_id = providerApi.baseUrl(), provider1_id = "padma coffee works")

      beforeEach {
        providerApi.resetAll()
        registryBppLookupApi.resetAll()
        stubBppLookupApi(registryBppLookupApi, providerApi)
        stubBppLookupApi(registryBppLookupApi, provider2Api)
      }

      it("should return error when BPP init call fails") {
        providerApi.stubFor(WireMock.post("/init").willReturn(WireMock.serverError()))

        val initializeOrderResponseString =
          invokeInitializeOrder(order).andExpect(MockMvcResultMatchers.status().isInternalServerError)
            .andReturn().response.contentAsString

        val initializeOrderResponse =
          verifyInitResponseMessage(
            initializeOrderResponseString,
            order,
            ResponseMessage.nack(),
            ProtocolError("BAP_011", "BPP returned error")
          )
        verifyThatMessageWasNotPersisted(initializeOrderResponse)
        verifyThatBppInitApiWasInvoked(initializeOrderResponse, order, providerApi)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, providerApi)
      }

      it("should validate that order contains items from only one bpp") {

        val orderWithMultipleBppItems =
          OrderDtoFactory.create(
            null,
            bpp1_id = providerApi.baseUrl(),
            bpp2_id = provider2Api.baseUrl(),
            provider1_id = "padma coffee works"
          )

        val initializeOrderResponseString = invokeInitializeOrder(orderWithMultipleBppItems)
          .andExpect(MockMvcResultMatchers.status().is4xxClientError)
          .andReturn()
          .response.contentAsString

        val initializeOrderResponse =
          verifyInitResponseMessage(
            initializeOrderResponseString,
            orderWithMultipleBppItems,
            ResponseMessage.nack(),
            ProtocolError("BAP_014", "More than one BPP's item(s) selected/initialized")
          )
        verifyThatMessageWasNotPersisted(initializeOrderResponse)
        verifyThatBppInitApiWasNotInvoked(providerApi)
        verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi)
        verifyThatSubscriberLookupApiWasNotInvoked(provider2Api)
      }

      it("should validate that order contains items from only one provider") {
        val orderWithMultipleProviderItems =
          OrderDtoFactory.create(
            null,
            bpp1_id = providerApi.baseUrl(),
            provider1_id = "padma coffee works",
            provider2_id = "Venugopal store"
          )

        val initializeOrderResponseString = invokeInitializeOrder(orderWithMultipleProviderItems)
          .andExpect(MockMvcResultMatchers.status().is4xxClientError)
          .andReturn()
          .response.contentAsString

        val initializeOrderResponse =
          verifyInitResponseMessage(
            initializeOrderResponseString,
            orderWithMultipleProviderItems,
            ResponseMessage.nack(),
            ProtocolError("BAP_010", "More than one Provider's item(s) selected/initialized")
          )
        verifyThatMessageWasNotPersisted(initializeOrderResponse)
        verifyThatBppInitApiWasNotInvoked(providerApi)
        verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi)
        verifyThatSubscriberLookupApiWasNotInvoked(provider2Api)
      }

      it("should invoke provide select api and save message") {
        providerApi
          .stubFor(
            WireMock.post("/init").willReturn(
              WireMock.okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory)))
            )
          )

        val initializeOrderResponseString = invokeInitializeOrder(order)
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val initializeOrderResponse =
          verifyInitResponseMessage(initializeOrderResponseString, order, ResponseMessage.ack())
        verifyThatMessageWasPersisted(initializeOrderResponse)
        verifyThatBppInitApiWasInvoked(initializeOrderResponse, order, providerApi)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, providerApi)
      }

      registryBppLookupApi.stop()

    }
  }

  private fun verifyThatSubscriberLookupApiWasInvoked(
    registryBppLookupApi: WireMockServer,
    bppApi: WireMockServer
  ) {
    registryBppLookupApi.verify(
      WireMock.postRequestedFor(WireMock.urlEqualTo("/lookup"))
        .withRequestBody(
          WireMock.equalToJson(
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

  private fun verifyThatBppInitApiWasNotInvoked(bppApi: WireMockServer) =
    bppApi.verify(0, WireMock.postRequestedFor(WireMock.urlEqualTo("/init")))

  private fun verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi: WireMockServer) =
    registryBppLookupApi.verify(0, WireMock.postRequestedFor(WireMock.urlEqualTo("/lookup")))

  private fun stubBppLookupApi(
    registryBppLookupApi: WireMockServer,
    providerApi: WireMockServer
  ) {
    registryBppLookupApi
      .stubFor(
        WireMock.post("/lookup")
          .withRequestBody(WireMock.matchingJsonPath("$.subscriber_id", WireMock.equalTo(providerApi.baseUrl())))
          .willReturn(WireMock.okJson(getSubscriberForBpp(providerApi)))
      )
  }

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

  private fun verifyInitResponseMessage(
    initializeOrderResponseString: String,
    order: OrderDto,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): ProtocolAckResponse {
    val initOrderResponse = objectMapper.readValue(initializeOrderResponseString, ProtocolAckResponse::class.java)
    initOrderResponse.context shouldNotBe null
    initOrderResponse.context?.messageId shouldNotBe null
    initOrderResponse.context?.transactionId shouldBe order.transactionId
    initOrderResponse.context?.action shouldBe ProtocolContext.Action.INIT
    initOrderResponse.message shouldBe expectedMessage
    initOrderResponse.error shouldBe expectedError
    return initOrderResponse
  }

  private fun verifyThatBppInitApiWasInvoked(
    initializeOrderResponse: ProtocolAckResponse,
    order: OrderDto,
    providerApi: WireMockServer
  ) {
    val protocolInitRequest = getProtocolInitRequest(initializeOrderResponse, order)
    providerApi.verify(
      WireMock.postRequestedFor(WireMock.urlEqualTo("/init"))
        .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(protocolInitRequest)))
    )
  }

  private fun getProtocolInitRequest(
    initializeOrderResponse: ProtocolAckResponse,
    order: OrderDto
  ): ProtocolInitRequest {
    val locations =
      order.items?.first()?.provider?.locations?.map { ProtocolSelectMessageSelectedProviderLocations(id = it) }
    val provider =
      order.items?.first()?.provider// todo: does this hold good even for order object or is this gotten from somewhere else?
    return ProtocolInitRequest(
      context = initializeOrderResponse.context!!,
      message = ProtocolInitRequestMessage(
        order = ProtocolOrder(
          provider = ProtocolSelectMessageSelectedProvider(
            id = provider!!.id,
            locations = locations
          ),
          items = order.items!!.map { ProtocolSelectMessageSelectedItems(id = it.id, quantity = it.quantity) },
          billing = order.billingInfo,
          fulfillment = ProtocolFulfillment(
            end = ProtocolFulfillmentEnd(
              contact = ProtocolContact(
                phone = order.deliveryInfo.phone,
                email = order.deliveryInfo.email
              ), location = order.deliveryInfo.deliveryLocation
            ),
            type = "home_delivery",
          ),
          addOns = emptyList(),
          offers = emptyList()
        )
      )
    )
  }

  private fun verifyThatMessageWasPersisted(initializeOrderResponse: ProtocolAckResponse) {
    val savedMessage = messageRepository.findOne(MessageDao::id eq initializeOrderResponse.context?.messageId)
    savedMessage shouldNotBe null
  }

  private fun verifyThatMessageWasNotPersisted(initializeOrderResponse: ProtocolAckResponse) {
    val savedMessage = messageRepository.findOne(MessageDao::id eq initializeOrderResponse.context?.messageId)
    savedMessage shouldBe null
  }

  private fun invokeInitializeOrder(order: OrderDto) = mockMvc.perform(
    MockMvcRequestBuilders.post("/client/v1/initialize_order").header(
      org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
    ).content(objectMapper.writeValueAsString(order))
  )
}