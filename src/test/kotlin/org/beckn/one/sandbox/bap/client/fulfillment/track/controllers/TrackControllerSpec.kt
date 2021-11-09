package org.beckn.one.sandbox.bap.client.fulfillment.track.controllers

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientContext
import org.beckn.one.sandbox.bap.client.shared.dtos.TrackRequestDto
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.common.Verifier
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class TrackControllerSpec @Autowired constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val contextFactory: ContextFactory,
    val uuidFactory: UuidFactory,
) : DescribeSpec() {
  private val verifier = Verifier(objectMapper)

  init {
    describe("Track") {
      MockNetwork.startAllSubscribers()

      beforeEach {
        MockNetwork.resetAllSubscribers()
        stubBppLookupApi(registryBppLookupApi, retailBengaluruBpp)
        stubBppLookupApi(registryBppLookupApi, anotherRetailBengaluruBpp)
      }

      it("should return error when bpp track call fails") {
        retailBengaluruBpp.stubFor(post("/track").willReturn(serverError()))

        val trackResponseString = invokeTrackApi(getTrackRequestDto())
          .andExpect(status().is5xxServerError)
          .andReturn().response.contentAsString

        val trackResponse =
          verifyResponseMessage(trackResponseString, ResponseMessage.nack(), BppError.Internal.error())
        verifyThatBppTrackApiWasInvoked(trackResponse, retailBengaluruBpp)
        verifier.verifyThatSubscriberLookupApiWasInvoked(
          registryBppLookupApi,
          retailBengaluruBpp
        )
      }

      it("should invoke provide track api and save message") {
        retailBengaluruBpp
          .stubFor(
            post("/track").willReturn(
              okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
            )
          )

        val trackResponseString = invokeTrackApi(getTrackRequestDto())
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val trackResponse = verifyResponseMessage(
          responseString = trackResponseString,
          expectedMessage = ResponseMessage.ack(),
          expectedError = null
        )
        verifyThatBppTrackApiWasInvoked(trackResponse, retailBengaluruBpp)
        verifier.verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, retailBengaluruBpp)
      }

      it("should return error for v2 when bpp track call fails") {
        retailBengaluruBpp.stubFor(post("/track").willReturn(serverError()))

        val trackResponseString = invokeTrackApiV2(listOf())
          .andExpect(status().is4xxClientError)
          .andReturn().response.contentAsString

        val getTrackResponse = objectMapper.readValue(trackResponseString, object : TypeReference<List<ProtocolAckResponse>>(){})
        getTrackResponse.first().context shouldBe null
        getTrackResponse.first().message shouldBe ResponseMessage.nack()
        getTrackResponse.first().error shouldBe BppError.BadRequestError.badRequestError
      }

      it("should invoke provide track api v2 and save message") {
        retailBengaluruBpp
          .stubFor(
            post("/track").willReturn(
              okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
            )
          )

        val trackResponseString = invokeTrackApiV2(listOf(getTrackRequestDto()))
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        verifyV2ResponseMessage(
          responseString = trackResponseString,
          expectedMessage = ResponseMessage.ack(),
          expectedError = null
        )
      }
    }
  }

  private fun invokeTrackApi(trackRequestDto: TrackRequestDto): ResultActions {
    return mockMvc
      .perform(
        MockMvcRequestBuilders.post("/client/v1/track")
          .content(objectMapper.writeValueAsString(trackRequestDto))
          .contentType(MediaType.APPLICATION_JSON)
      )
  }

  private fun invokeTrackApiV2(trackRequestDtoList: List<TrackRequestDto>): ResultActions {
    return mockMvc
      .perform(
        MockMvcRequestBuilders.post("/client/v2/track")
          .content(objectMapper.writeValueAsString(trackRequestDtoList))
          .contentType(MediaType.APPLICATION_JSON)
      )
  }

  private fun getTrackRequestDto(bppId: String = retailBengaluruBpp.baseUrl()): TrackRequestDto {
    return TrackRequestDto(
      context = ClientContext(bppId = bppId, transactionId = uuidFactory.create()),
      message = ProtocolTrackRequestMessage(orderId = "order id 1"),
    )
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

  private fun verifyV2ResponseMessage(
    responseString: String,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): List<ProtocolAckResponse> {
    val getTrackResponse = objectMapper.readValue(responseString, object : TypeReference<List<ProtocolAckResponse>>(){})
    getTrackResponse.first().context shouldNotBe null
    getTrackResponse.first().context?.messageId shouldNotBe null
    getTrackResponse.first().context?.action shouldBe ProtocolContext.Action.TRACK
    getTrackResponse.first().message shouldBe expectedMessage
    getTrackResponse.first().error shouldBe expectedError
    return getTrackResponse
  }

  private fun verifyResponseMessage(
    responseString: String,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): ProtocolAckResponse {
    val getTrackResponse = objectMapper.readValue(responseString, ProtocolAckResponse::class.java)
    getTrackResponse.context shouldNotBe null
    getTrackResponse.context?.messageId shouldNotBe null
    getTrackResponse.context?.action shouldBe ProtocolContext.Action.TRACK
    getTrackResponse.message shouldBe expectedMessage
    getTrackResponse.error shouldBe expectedError
    return getTrackResponse
  }

  private fun verifyThatBppTrackApiWasInvoked(
    trackResponse: ProtocolAckResponse,
    bppApi: WireMockServer
  ) {
    val protocolTrackRequest = getProtocolTrackRequest(trackResponse)
    bppApi.verify(
      postRequestedFor(urlEqualTo("/track"))
        .withRequestBody(equalToJson(objectMapper.writeValueAsString(protocolTrackRequest)))
    )
  }

  private fun getProtocolTrackRequest(trackResponse: ProtocolAckResponse): ProtocolTrackRequest {
    return ProtocolTrackRequest(
      context = trackResponse.context!!,
      message = ProtocolTrackRequestMessage(orderId = "order id 1"),
    )
  }
}
