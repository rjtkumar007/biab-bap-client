package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.dtos.ClientContext
import org.beckn.one.sandbox.bap.client.dtos.TrackRequestDto
import org.beckn.one.sandbox.bap.client.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.common.factories.SubscriberDtoFactory
import org.beckn.one.sandbox.bap.message.entities.MessageDao
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
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
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class TrackControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val contextFactory: ContextFactory,
  val uuidFactory: UuidFactory,
  val messageRepository: GenericRepository<MessageDao>
) : DescribeSpec() {
  init {
    describe("Track") {
      val verifier = Verifier(objectMapper, messageRepository)
      val registryBppLookupApi = WireMockServer(4010)
      val bppApi = WireMockServer(4011)
      val anotherBppApi = WireMockServer(4012)
      bppApi.start()
      registryBppLookupApi.start()
      anotherBppApi.start()

      beforeEach {
        bppApi.resetAll()
        registryBppLookupApi.resetAll()
        stubBppLookupApi(registryBppLookupApi, bppApi)
        stubBppLookupApi(registryBppLookupApi, anotherBppApi)
      }

      it("should return error when bpp track call fails") {
        bppApi.stubFor(post("/track").willReturn(serverError()))

        val trackResponseString = invokeTrackApi(getTrackRequestDto(bppApi.baseUrl()))
          .andExpect(status().is5xxServerError)
          .andReturn().response.contentAsString

        val getQuoteResponse =
          verifyResponseMessage(trackResponseString, ResponseMessage.nack(), BppError.Internal.error())
        verifier.verifyThatMessageWasNotPersisted(getQuoteResponse)
        verifyThatBppTrackApiWasInvoked(getQuoteResponse, bppApi)
        verifier.verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, bppApi)
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

  private fun getTrackRequestDto(bppId: String): TrackRequestDto {
    return TrackRequestDto(
      context = ClientContext(bppId = bppId, transactionId = ""),
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

  private fun verifyResponseMessage(
    trackResponseString: String,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): ProtocolAckResponse {
    val getQuoteResponse = objectMapper.readValue(trackResponseString, ProtocolAckResponse::class.java)
    getQuoteResponse.context shouldNotBe null
    getQuoteResponse.context?.messageId shouldNotBe null
    getQuoteResponse.context?.action shouldBe ProtocolContext.Action.TRACK
    getQuoteResponse.message shouldBe expectedMessage
    getQuoteResponse.error shouldBe expectedError
    return getQuoteResponse
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
