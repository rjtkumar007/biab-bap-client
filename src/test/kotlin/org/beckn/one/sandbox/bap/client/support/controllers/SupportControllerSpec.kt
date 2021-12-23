package org.beckn.one.sandbox.bap.client.support.controllers

import arrow.core.Either
import arrow.core.left
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.beckn.one.sandbox.bap.client.external.domains.Subscriber
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberLookupRequest
import org.beckn.one.sandbox.bap.client.shared.dtos.*
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.support.services.SupportService
import org.beckn.one.sandbox.bap.common.City
import org.beckn.one.sandbox.bap.common.Country
import org.beckn.one.sandbox.bap.common.Domain
import org.beckn.one.sandbox.bap.common.factories.MockNetwork
import org.beckn.one.sandbox.bap.common.factories.ResponseFactory
import org.beckn.one.sandbox.bap.common.factories.SubscriberDtoFactory
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.protocol.schemas.*
import org.litote.kmongo.util.idValue
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
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
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class SupportControllerSpec @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val contextFactory: ContextFactory,
    val uuidFactory: UuidFactory,
) : DescribeSpec() {
  init {
    describe("Get support details from BPP") {
      MockNetwork.startAllSubscribers()
      val context =
        ClientContext(transactionId = uuidFactory.create(), bppId = MockNetwork.retailBengaluruBpp.baseUrl())
      val supportRequest = SupportRequestDto(
        context = context,
        message = SupportRequestMessage(
          refId = "abc123"
        ),
      )

      beforeEach {
        MockNetwork.resetAllSubscribers()
        stubBppLookupApi(MockNetwork.registryBppLookupApi, MockNetwork.retailBengaluruBpp)
      }

      it("should return error when BPP support call fails") {
        MockNetwork.retailBengaluruBpp.stubFor(WireMock.post("/support").willReturn(WireMock.serverError()))

        val supportResponseString =
          invokeSupportOrder(supportRequest).andExpect(MockMvcResultMatchers.status().isInternalServerError)
            .andReturn().response.contentAsString

        val supportResponse =
          verifySupportResponseMessage(
            supportResponseString,
            supportRequest,
            ResponseMessage.nack(),
            ProtocolError("BAP_011", "BPP returned error")
          )
        verifyThatBppSupportApiWasInvoked(supportResponse, supportRequest, MockNetwork.retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasInvoked(MockNetwork.registryBppLookupApi, MockNetwork.retailBengaluruBpp)
      }

      it("should invoke provide support api and save message") {
        MockNetwork.retailBengaluruBpp
          .stubFor(
            WireMock.post("/support").willReturn(
              WireMock.okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
            )
          )

        val supportResponseString = invokeSupportOrder(supportRequest)
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val supportResponse =
          verifySupportResponseMessage(supportResponseString, supportRequest, ResponseMessage.ack())
        verifyThatBppSupportApiWasInvoked(supportResponse, supportRequest, MockNetwork.retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasInvoked(MockNetwork.registryBppLookupApi, MockNetwork.retailBengaluruBpp)
      }

      it("should invoke provide support api v2 and empty list throw error bad request") {
          invokeSupportOrderV2(listOf()).andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andReturn().response.contentAsString
      }

        it("should invoke provide support v2 api and save message") {
          val supportRequestList = listOf(
            SupportRequestDto(
              context = context,
              message = SupportRequestMessage(
                refId = "abc123"
              ),
            )
          )
          MockNetwork.retailBengaluruBpp
            .stubFor(
              WireMock.post("/support").willReturn(
                WireMock.okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
              )
            )

          val supportResponseV2String = invokeSupportOrderV2(supportRequestList)
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andReturn()
            .response.contentAsString

            verifySupportV2ResponseMessage(supportResponseV2String, supportRequestList, ResponseMessage.ack())

        }
      MockNetwork.registryBppLookupApi.stop()

    }
  }

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

  private fun invokeSupportOrder(supportRequest: SupportRequestDto) = mockMvc.perform(
    MockMvcRequestBuilders.post("/client/v1/get_support").header(
      org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
    ).content(objectMapper.writeValueAsString(supportRequest)).param("signature", "abc")
  )

  private fun invokeSupportOrderV2(supportRequestList: List<SupportRequestDto>) = mockMvc.perform(
    MockMvcRequestBuilders.post("/client/v2/get_support").header(
      org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
    ).content(objectMapper.writeValueAsString(supportRequestList)).param("signature", "abc")
  )

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

  private fun verifySupportV2ResponseMessage(
    supportResponseString: String,
    supportRequest: List<SupportRequestDto>,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): List<ProtocolAckResponse> {
    val supportOrderResponse = objectMapper.readValue(supportResponseString, object : TypeReference<List<ProtocolAckResponse>>(){})
    supportOrderResponse.first().context shouldNotBe null
    supportOrderResponse.first().context?.messageId shouldNotBe null
    supportOrderResponse.first().context?.transactionId shouldBe supportRequest.first().context.transactionId
    supportOrderResponse.first().context?.action shouldBe ProtocolContext.Action.SUPPORT
    supportOrderResponse.first().message shouldBe expectedMessage
    supportOrderResponse.first().error shouldBe expectedError
    return supportOrderResponse
  }


  private fun verifySupportResponseMessage(
    supportResponseString: String,
    supportRequest: SupportRequestDto,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): ProtocolAckResponse {
    val supportOrderResponse = objectMapper.readValue(supportResponseString, ProtocolAckResponse::class.java)
    supportOrderResponse.context shouldNotBe null
    supportOrderResponse.context?.messageId shouldNotBe null
    supportOrderResponse.context?.transactionId shouldBe supportRequest.context.transactionId
    supportOrderResponse.context?.action shouldBe ProtocolContext.Action.SUPPORT
    supportOrderResponse.message shouldBe expectedMessage
    supportOrderResponse.error shouldBe expectedError
    return supportOrderResponse
  }

  private fun verifyThatBppSupportApiWasInvoked(
    supportResponse: ProtocolAckResponse,
    supportRequest: SupportRequestDto,
    providerApi: WireMockServer
  ) {
    val protocolSupportRequest = getProtocolSupportRequest(supportResponse, supportRequest)
    providerApi.verify(
      WireMock.postRequestedFor(WireMock.urlEqualTo("/support"))
        .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(protocolSupportRequest)))
    )
  }

  private fun verifyThatBppSupportV2ApiWasInvoked(
    supportResponse: List<ProtocolAckResponse>,
    supportRequest: List<SupportRequestDto>,
    providerApi: WireMockServer
  ) {
    val protocolSupportRequest = getProtocolSupportRequestV2(supportResponse, supportRequest)
    providerApi.verify(
      WireMock.postRequestedFor(WireMock.urlEqualTo("/support"))
        .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(protocolSupportRequest)))
    )
  }
  private fun getProtocolSupportRequestV2(
    supportResponse: List<ProtocolAckResponse>,
    supportRequest: List<SupportRequestDto>
  ): List<ProtocolSupportRequest> = listOf(ProtocolSupportRequest(
    context = supportResponse.first().context!!,
    message = ProtocolSupportRequestMessage(
      refId = supportRequest.first().message.refId
    ))
  )
  private fun getProtocolSupportRequest(
    supportResponse: ProtocolAckResponse,
    supportRequest: SupportRequestDto
  ): ProtocolSupportRequest = ProtocolSupportRequest(
    context = supportResponse.context!!,
    message = ProtocolSupportRequestMessage(
      refId = supportRequest.message.refId
    )
  )

}