package org.beckn.one.sandbox.bap.client.order.status.controllers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.apache.http.HttpHeaders.CONTENT_TYPE
import org.beckn.one.sandbox.bap.client.external.domains.Subscriber
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberLookupRequest
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientContext
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderStatusDto
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class OrderStatusControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val contextFactory: ContextFactory,
  val uuidFactory: UuidFactory
) : DescribeSpec() {
  init {
    describe("Get Order status from BPP") {
      MockNetwork.startAllSubscribers()
      val context = ClientContext(transactionId = uuidFactory.create(), bppId = retailBengaluruBpp.baseUrl())
      val orderId = "order id 1"
      beforeEach {
        MockNetwork.resetAllSubscribers()
        stubBppLookupApi(registryBppLookupApi, retailBengaluruBpp)
        stubBppLookupApi(registryBppLookupApi, anotherRetailBengaluruBpp)
      }

      it("should return error when BPP order status call fails") {
        retailBengaluruBpp.stubFor(post("/status").willReturn(serverError()))

        val orderStatusResponseString =
          invokeOrderStatusApi(getOrderStatusRequest(clientContext = context, orderId = orderId))
            .andExpect(status().isInternalServerError)
            .andReturn().response.contentAsString

        val orderStatusResponse =
          verifyOrderStatusResponseMessage(
            orderStatusResponseString,
            ResponseMessage.nack(),
            ProtocolError("BAP_011", "BPP returned error")
          )
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, retailBengaluruBpp)
        verifyThatBppOrderStatusApiWasInvoked(orderStatusResponse, orderId, retailBengaluruBpp)
      }

      it("should return ack response when order status api invocation is successful") {
        retailBengaluruBpp
          .stubFor(
            post("/status")
              .willReturn(okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create()))))
          )

        val orderStatusResponseString =
          invokeOrderStatusApi(getOrderStatusRequest(clientContext = context, orderId = orderId))
            .andExpect(status().is2xxSuccessful)
            .andReturn()
            .response.contentAsString

        val orderStatusResponse =
          verifyOrderStatusResponseMessage(orderStatusResponseString, ResponseMessage.ack())
        verifyThatBppOrderStatusApiWasInvoked(orderStatusResponse, orderId, retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, retailBengaluruBpp)
      }

     it("should return error when empty request is passed to the api v2") {
        val orderStatusEmptyResponseString =
          invokeOrderStatusApiV2(listOf())
            .andExpect(status().is4xxClientError)
            .andReturn().response.contentAsString

        val orderStatusEmptyResponse =
          objectMapper.readValue(orderStatusEmptyResponseString, object : TypeReference<List<ProtocolAckResponse>>() {})
       orderStatusEmptyResponse.first().context shouldBe null
       orderStatusEmptyResponse.first().message shouldBe ResponseMessage.nack()
       orderStatusEmptyResponse.first().error shouldBe BppError.BadRequestError.error()
      }

      it("should return ack response when order status return error") {
        retailBengaluruBpp.stubFor(post("/status").willReturn(serverError()))
        val orderStatusResponseStringWithError =
          invokeOrderStatusApiV2(listOf(getOrderStatusRequest(clientContext = context, orderId = orderId)))
            .andExpect(status().is2xxSuccessful)
            .andReturn()
            .response.contentAsString
          verifyOrderStatusResponseMessageV2(orderStatusResponseStringWithError, ResponseMessage.nack(),ProtocolError("BAP_011", "BPP returned error"))
      }

      it("should return ack response when order status api v2 invocation is successful") {
        retailBengaluruBpp
          .stubFor(
            post("/status")
              .willReturn(okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create()))))
          )

        val orderStatusResponseString =
          invokeOrderStatusApiV2(listOf(getOrderStatusRequest(clientContext = context, orderId = orderId)))
            .andExpect(status().is2xxSuccessful)
            .andReturn()
            .response.contentAsString

        val orderStatusResponse =
          verifyOrderStatusResponseMessageV2(orderStatusResponseString, ResponseMessage.ack())
        verifyThatBppOrderStatusV2ApiWasInvoked(orderStatusResponse, orderId, retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, retailBengaluruBpp)
      }
    }
  }


  private fun verifyThatBppOrderStatusV2ApiWasInvoked(
    orderStatusResponse: List<ProtocolAckResponse>,
    orderId: String,
    providerApi: WireMockServer
  ) {
    val protocolOrderStatusRequest = getProtocolOrderStatusV2Request(orderStatusResponse, orderId)
    providerApi.verify(
      postRequestedFor(urlEqualTo("/status"))
        .withRequestBody(equalToJson(objectMapper.writeValueAsString(protocolOrderStatusRequest)))
    )
  }

  private fun getProtocolOrderStatusV2Request(
    orderStatusResponse: List<ProtocolAckResponse>,
    orderId: String
  ): ProtocolOrderStatusRequest {
    return ProtocolOrderStatusRequest(
      context = orderStatusResponse.first().context!!,
      message = ProtocolOrderStatusRequestMessage(orderId = orderId)
    )
  }

  private fun verifyOrderStatusResponseMessageV2(
    orderStatusResponseString: String,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): List<ProtocolAckResponse> {
    val orderStatusResponse =
      objectMapper.readValue(orderStatusResponseString, object : TypeReference<List<ProtocolAckResponse>>() {})
    orderStatusResponse.first().context shouldNotBe null
    orderStatusResponse.first().context?.messageId shouldNotBe null
    orderStatusResponse.first().context?.action shouldBe ProtocolContext.Action.STATUS
    orderStatusResponse.first().message shouldBe expectedMessage
    orderStatusResponse.first().error shouldBe expectedError
    return orderStatusResponse
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

  private fun stubBppLookupApi(registryBppLookupApi: WireMockServer, providerApi: WireMockServer) {
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

  private fun verifyOrderStatusResponseMessage(
    orderStatusResponseString: String,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): ProtocolAckResponse {
    val orderStatusResponse = objectMapper.readValue(orderStatusResponseString, ProtocolAckResponse::class.java)
    orderStatusResponse.context shouldNotBe null
    orderStatusResponse.context?.messageId shouldNotBe null
    orderStatusResponse.context?.action shouldBe ProtocolContext.Action.STATUS
    orderStatusResponse.message shouldBe expectedMessage
    orderStatusResponse.error shouldBe expectedError
    return orderStatusResponse
  }

  private fun verifyThatBppOrderStatusApiWasInvoked(
    orderStatusResponse: ProtocolAckResponse,
    orderId: String,
    providerApi: WireMockServer
  ) {
    val protocolOrderStatusRequest = getProtocolOrderStatusRequest(orderStatusResponse, orderId)
    providerApi.verify(
      postRequestedFor(urlEqualTo("/status"))
        .withRequestBody(equalToJson(objectMapper.writeValueAsString(protocolOrderStatusRequest)))
    )
  }

  private fun getProtocolOrderStatusRequest(
    orderStatusResponse: ProtocolAckResponse,
    orderId: String
  ): ProtocolOrderStatusRequest {
    return ProtocolOrderStatusRequest(
      context = orderStatusResponse.context!!,
      message = ProtocolOrderStatusRequestMessage(orderId = orderId)
    )
  }

  private fun getOrderStatusRequest(
    clientContext: ClientContext,
    orderId: String
  ): OrderStatusDto {
    return OrderStatusDto(
      context = clientContext,
      message = ProtocolOrderStatusRequestMessage(orderId = orderId)
    )
  }

  private fun invokeOrderStatusApi(orderStatusDto: OrderStatusDto) = mockMvc.perform(
    MockMvcRequestBuilders
      .post("/client/v1/order_status")
      .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).content(objectMapper.writeValueAsString(orderStatusDto))
  )

  private fun invokeOrderStatusApiV2(orderStatusDtoList: List<OrderStatusDto>) = mockMvc.perform(
    MockMvcRequestBuilders
      .post("/client/v2/order_status")
      .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .content(objectMapper.writeValueAsString(orderStatusDtoList))
  )
}