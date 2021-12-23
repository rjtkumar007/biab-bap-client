package org.beckn.one.sandbox.bap.client.order.init.controllers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.external.domains.Subscriber
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberLookupRequest
import org.beckn.one.sandbox.bap.client.factories.OrderDtoFactory
import org.beckn.one.sandbox.bap.client.factories.OrderItemDtoFactory
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientContext
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderRequestDto
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.common.City
import org.beckn.one.sandbox.bap.common.Country
import org.beckn.one.sandbox.bap.common.Domain
import org.beckn.one.sandbox.bap.common.factories.MockNetwork
import org.beckn.one.sandbox.bap.common.factories.MockNetwork.anotherRetailBengaluruBpp
import org.beckn.one.sandbox.bap.common.factories.MockNetwork.registryBppLookupApi
import org.beckn.one.sandbox.bap.common.factories.MockNetwork.retailBengaluruBpp
import org.beckn.one.sandbox.bap.common.factories.ResponseFactory
import org.beckn.one.sandbox.bap.common.factories.SubscriberDtoFactory
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.protocol.schemas.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class InitOrderControllerSpec @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val contextFactory: ContextFactory,
    val uuidFactory: UuidFactory,
) : DescribeSpec() {
  init {
    describe("Initialize order with BPP") {
      MockNetwork.startAllSubscribers()
      val context = ClientContext(transactionId = uuidFactory.create())
      val orderRequest = OrderRequestDto(
        context = context,
        message = OrderDtoFactory.create(
          bpp1_id = retailBengaluruBpp.baseUrl(),
          provider1_id = "padma coffee works"
        ),
      )
      val orderRequestList = listOf(OrderRequestDto(
        context = context,
        message = OrderDtoFactory.create(
          bpp1_id = retailBengaluruBpp.baseUrl(),
          provider1_id = "padma coffee works"
        ),
      ))

      beforeEach {
        MockNetwork.resetAllSubscribers()
        stubBppLookupApi(registryBppLookupApi, retailBengaluruBpp)
        stubBppLookupApi(registryBppLookupApi, anotherRetailBengaluruBpp)
      }

      it("should return error when BPP init call fails") {
        retailBengaluruBpp.stubFor(post("/init").willReturn(serverError()))

        val initOrderResponseString =
          invokeInitOrder(orderRequest).andExpect(MockMvcResultMatchers.status().isInternalServerError)
            .andReturn().response.contentAsString

        val initOrderResponse =
          verifyInitResponseMessage(
            initOrderResponseString,
            orderRequest,
            ResponseMessage.nack(),
            ProtocolError("BAP_011", "BPP returned error")
          )
        verifyThatBppInitApiWasInvoked(initOrderResponse, orderRequest, retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, retailBengaluruBpp)
      }

      it("should validate that order contains items from only one bpp") {

        val orderRequestWithMultipleBppItems =
          OrderRequestDto(
            context = context,
            message = OrderDtoFactory.create(
              null,
              bpp1_id = retailBengaluruBpp.baseUrl(),
              bpp2_id = anotherRetailBengaluruBpp.baseUrl(),
              provider1_id = "padma coffee works"
            )
          )

        val initOrderResponseString = invokeInitOrder(orderRequestWithMultipleBppItems)
          .andExpect(MockMvcResultMatchers.status().is4xxClientError)
          .andReturn()
          .response.contentAsString

        verifyInitResponseMessage(
          initOrderResponseString,
          orderRequestWithMultipleBppItems,
          ResponseMessage.nack(),
          ProtocolError("BAP_014", "More than one BPP's item(s) selected/initialized")
        )
        verifyThatBppInitApiWasNotInvoked(retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi)
        verifyThatSubscriberLookupApiWasNotInvoked(anotherRetailBengaluruBpp)
      }

      it("should validate that order contains items from only one provider") {
        val orderRequestWithMultipleProviderItems = OrderRequestDto(
          context = context,
          message = OrderDtoFactory.create(
            null,
            bpp1_id = retailBengaluruBpp.baseUrl(),
            provider1_id = "padma coffee works",
            provider2_id = "Venugopal store"
          )
        )

        val initOrderResponseString = invokeInitOrder(orderRequestWithMultipleProviderItems)
          .andExpect(MockMvcResultMatchers.status().is4xxClientError)
          .andReturn()
          .response.contentAsString

        verifyInitResponseMessage(
          initOrderResponseString,
          orderRequestWithMultipleProviderItems,
          ResponseMessage.nack(),
          ProtocolError("BAP_010", "More than one Provider's item(s) selected/initialized")
        )
        verifyThatBppInitApiWasNotInvoked(retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi)
        verifyThatSubscriberLookupApiWasNotInvoked(anotherRetailBengaluruBpp)
      }

      it("should return null when cart items are empty") {
        val initRequestForTest = OrderRequestDto(
          message = OrderDtoFactory.create(
            bpp1_id = retailBengaluruBpp.baseUrl(),
            provider1_id = "padma coffee works",
            items = emptyList()
          ), context = context
        )
        retailBengaluruBpp
          .stubFor(
            post("/init").willReturn(
              okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
            )
          )

        val initOrderResponseString = invokeInitOrder(initRequestForTest)
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        verifyInitResponseMessage(initOrderResponseString, initRequestForTest, ResponseMessage.ack())
        verifyThatBppInitApiWasNotInvoked(retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi)
      }

      it("should invoke provide init api and save message") {
        val initRequestForTest = OrderRequestDto(
          message = OrderDtoFactory.create(
            bpp1_id = retailBengaluruBpp.baseUrl(),
            provider1_id = "padma coffee works",
            items = listOf(
              OrderItemDtoFactory.create(
                providerId = "padma coffee works",
                bppId = retailBengaluruBpp.baseUrl()
              )
            )
          ), context = context
        )
        retailBengaluruBpp
          .stubFor(
            post("/init").willReturn(
              okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
            )
          )

        val initOrderResponseString = invokeInitOrder(initRequestForTest)
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val confirmOrderResponse =
          verifyInitResponseMessage(initOrderResponseString, initRequestForTest, ResponseMessage.ack())
        verifyThatBppInitApiWasInvoked(confirmOrderResponse, initRequestForTest, retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, retailBengaluruBpp)
      }

      it("should return error when BPP init v2 empty request") {

        val initOrderResponseString =
          invokeInitOrderV2(listOf()).andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andReturn().response.contentAsString
        val clientResponse = objectMapper.readValue(initOrderResponseString, object : TypeReference<List<ProtocolAckResponse>>(){})
        clientResponse.first().context shouldBe  null
        clientResponse.first().message shouldBe  ResponseMessage.nack()
        clientResponse.first().error shouldBe BppError.BadRequestError.badRequestError
      }

      it("should return error when BPP init v2 fails") {
        retailBengaluruBpp.stubFor(post("/init").willReturn(serverError()))
        val initOrderResponseString =
          invokeInitOrderV2(orderRequestList).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andReturn().response.contentAsString
        val initOrderResponse =
          verifyInitResponseMessageV2(
            initOrderResponseString,
            orderRequestList.first(),
            ResponseMessage.nack(),
            ProtocolError("BAP_011", "BPP returned error")
          )
        verifyThatBppInitApiWasInvokedV2(initOrderResponse, orderRequestList.first(), retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, retailBengaluruBpp)
      }

      it("should return success when BPP init v2 success") {
        retailBengaluruBpp.stubFor(post("/init").willReturn(
          okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
        ))
        val initOrderResponseString =
          invokeInitOrderV2(orderRequestList).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andReturn().response.contentAsString
        val initOrderResponse =
          verifyInitResponseMessageV2(
            initOrderResponseString,
            orderRequestList.first(),
            ResponseMessage.ack()
          )
        verifyThatBppInitApiWasInvokedV2(initOrderResponse, orderRequestList.first(), retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, retailBengaluruBpp)
      }

      registryBppLookupApi.stop()

    }
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

  private fun verifyThatBppInitApiWasNotInvoked(bppApi: WireMockServer) =
    bppApi.verify(0, postRequestedFor(urlEqualTo("/init")))

  private fun verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi: WireMockServer) =
    registryBppLookupApi.verify(0, postRequestedFor(urlEqualTo("/lookup")))

  private fun stubBppLookupApi(
    registryBppLookupApi: WireMockServer,
    providerApi: WireMockServer
  ) {
    registryBppLookupApi
      .stubFor(
        post("/lookup")
          .withRequestBody(matchingJsonPath("$.subscriber_id", equalTo(providerApi.baseUrl())))
          .willReturn(okJson(getSubscriberForBpp(providerApi)))
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
    initOrderResponseString: String,
    orderRequest: OrderRequestDto,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): ProtocolAckResponse {
    val initOrderResponse = objectMapper.readValue(initOrderResponseString, ProtocolAckResponse::class.java)
    initOrderResponse.context shouldNotBe null
    initOrderResponse.context?.messageId shouldNotBe null
    initOrderResponse.context?.transactionId shouldBe orderRequest.context.transactionId
    initOrderResponse.context?.action shouldBe ProtocolContext.Action.INIT
    initOrderResponse.message shouldBe expectedMessage
    initOrderResponse.error shouldBe expectedError
    return initOrderResponse
  }

  private fun verifyThatBppInitApiWasInvoked(
    initOrderResponse: ProtocolAckResponse,
    orderRequest: OrderRequestDto,
    providerApi: WireMockServer
  ) {
    val protocolInitRequest = getProtocolInitRequest(initOrderResponse, orderRequest)
    providerApi.verify(
      postRequestedFor(urlEqualTo("/init"))
        .withRequestBody(equalToJson(objectMapper.writeValueAsString(protocolInitRequest)))
    )
  }

  private fun getProtocolInitRequest(
    initOrderResponse: ProtocolAckResponse,
    orderRequest: OrderRequestDto
  ): ProtocolInitRequest {
    val locations =
      orderRequest.message.items?.first()?.provider?.locations?.map { ProtocolSelectMessageSelectedProviderLocations(id = it) }
    val provider =
      orderRequest.message.items?.first()?.provider// todo: does this hold good even for order object or is this gotten from somewhere else?
    return ProtocolInitRequest(
      context = initOrderResponse.context!!,
      message = ProtocolInitRequestMessage(
        order = ProtocolOrder(
          provider = ProtocolSelectMessageSelectedProvider(
            id = provider!!.id,
            locations = locations
          ),
          items = orderRequest.message.items!!.map {
            ProtocolSelectMessageSelectedItems(
              id = it.id,
              quantity = it.quantity
            )
          },
          billing = orderRequest.message.billingInfo,
          fulfillment = ProtocolFulfillment(
            provider_id = "padma coffee works",
            end = ProtocolFulfillmentEnd(
              contact = ProtocolContact(
                phone = orderRequest.message.deliveryInfo.phone,
                email = orderRequest.message.deliveryInfo.email
              ), location = orderRequest.message.deliveryInfo.location
            ),
            type = "home-delivery",
            customer = ProtocolCustomer(person = ProtocolPerson(name = orderRequest.message.deliveryInfo.name))
          ),
          addOns = emptyList(),
          offers = emptyList()
        )
      )
    )
  }

  private fun invokeInitOrder(orderRequest: OrderRequestDto) = mockMvc.perform(
    MockMvcRequestBuilders.post("/client/v1/initialize_order").header(
      org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
    ).content(objectMapper.writeValueAsString(orderRequest))
  )

  private fun invokeInitOrderV2(orderRequest: List<OrderRequestDto>): ResultActions {
    return mockMvc.perform(
      MockMvcRequestBuilders.post("/client/v2/initialize_order").header(
        org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
      ).content(objectMapper.writeValueAsString(orderRequest))
    )
  }

  private fun verifyInitResponseMessageV2(
    initOrderResponseString: String,
    orderRequest: OrderRequestDto,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): List<ProtocolAckResponse> {
    val initOrderResponse = objectMapper.readValue(initOrderResponseString, object : TypeReference<List<ProtocolAckResponse>>(){})
    initOrderResponse.first().context shouldNotBe null
    initOrderResponse.first().context?.messageId shouldNotBe null
    initOrderResponse.first().context?.transactionId shouldBe orderRequest.context.transactionId
    initOrderResponse.first().context?.action shouldBe ProtocolContext.Action.INIT
    initOrderResponse.first().message shouldBe expectedMessage
    initOrderResponse.first().error shouldBe expectedError
    return initOrderResponse
  }

  private fun verifyThatBppInitApiWasInvokedV2(
    initOrderResponse: List<ProtocolAckResponse>,
    orderRequest: OrderRequestDto,
    providerApi: WireMockServer
  ) {
    val protocolInitRequest = getProtocolInitRequestV2(initOrderResponse, orderRequest)
    providerApi.verify(
      postRequestedFor(urlEqualTo("/init"))
        .withRequestBody(equalToJson(objectMapper.writeValueAsString(protocolInitRequest)))
    )
  }

  private fun getProtocolInitRequestV2(
    initOrderResponse: List<ProtocolAckResponse>,
    orderRequest: OrderRequestDto
  ): ProtocolInitRequest {
    val locations =
      orderRequest.message.items?.first()?.provider?.locations?.map { ProtocolSelectMessageSelectedProviderLocations(id = it) }
    val provider =
      orderRequest.message.items?.first()?.provider// todo: does this hold good even for order object or is this gotten from somewhere else?
    return ProtocolInitRequest(
      context = initOrderResponse.first().context!!,
      message = ProtocolInitRequestMessage(
        order = ProtocolOrder(
          provider = ProtocolSelectMessageSelectedProvider(
            id = provider!!.id,
            locations = locations
          ),
          items = orderRequest.message.items!!.map {
            ProtocolSelectMessageSelectedItems(
              id = it.id,
              quantity = it.quantity
            )
          },
          billing = orderRequest.message.billingInfo,
          fulfillment = ProtocolFulfillment(
            provider_id = "padma coffee works",
            end = ProtocolFulfillmentEnd(
              contact = ProtocolContact(
                phone = orderRequest.message.deliveryInfo.phone,
                email = orderRequest.message.deliveryInfo.email
              ), location = orderRequest.message.deliveryInfo.location
            ),
            type = "home-delivery",
            customer = ProtocolCustomer(person = ProtocolPerson(name = orderRequest.message.deliveryInfo.name))
          ),
          addOns = emptyList(),
          offers = emptyList()
        )
      )
    )
  }
}