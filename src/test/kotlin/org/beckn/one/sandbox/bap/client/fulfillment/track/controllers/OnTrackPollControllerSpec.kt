package org.beckn.one.sandbox.bap.client.fulfillment.track.controllers

import arrow.core.Either
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientSupportResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientTrackResponse
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.common.factories.MockProtocolBap
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.message.factories.ProtocolOnTrackMessageTrackingFactory
import org.beckn.protocol.schemas.ProtocolOnTrack
import org.beckn.protocol.schemas.ProtocolOnTrackMessage
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
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
internal class OnTrackPollControllerSpec @Autowired constructor(
    private val contextFactory: ContextFactory,
    private val mapper: ObjectMapper,
    private val protocolClient: ProtocolClient,
    private val mockMvc: MockMvc
) : DescribeSpec() {

  val context = contextFactory.create()
  private val protocolOnTrack = ProtocolOnTrack(
    context,
    message = ProtocolOnTrackMessage(tracking = ProtocolOnTrackMessageTrackingFactory.create())
  )
  val mockProtocolBap = MockProtocolBap.withResetInstance()

  init {
    describe("OnTrack callback") {

      context("when called for given message id") {
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v1/on_track?messageId=${context.messageId}")
            .willReturn(WireMock.okJson(mapper.writeValueAsString(trackResults())))
        )
        val onTrackCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v1/on_track")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageId", context.messageId)
          )

        it("should respond with status ok") {
          onTrackCall.andExpect(status().isOk)
        }

        it("should respond with all track responses in body") {
          val results = onTrackCall.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, ClientTrackResponse::class.java)
          clientResponse.message?.tracking shouldNotBe null
          clientResponse.message?.tracking shouldBe protocolOnTrack.message?.tracking
        }
      }

      context("when failure occurs during request processing") {
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnTrack, ClientTrackResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnRead))
        }
        val onTrackPollController = OnTrackPollController(mockOnPollService, contextFactory, protocolClient)
        it("should respond with failure") {
          val response = onTrackPollController.onTrack(context.messageId)
          response.statusCode shouldBe DatabaseError.OnRead.status()
        }
      }

      context("when called for given message ids of v2  ") {
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v2/on_track?messageIds=${context.messageId}")
            .willReturn(WireMock.okJson(mapper.writeValueAsString(trackResults())))
        )
        val onTrackCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v2/on_track")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageIds", context.messageId)
          )

        it("should respond with status ok") {
          onTrackCall.andExpect(status().isOk)
        }

        it("should respond with all track responses in body") {
          val results = onTrackCall.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, object : TypeReference<List<ClientTrackResponse>>(){})
          clientResponse.first().message?.tracking shouldNotBe null
          clientResponse.first().message?.tracking shouldBe protocolOnTrack.message?.tracking
        }
      }

      context("when failure occurs during request processing for v2") {
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnTrack, ClientTrackResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnRead))
        }
        val onTrackPollController = OnTrackPollController(mockOnPollService, contextFactory, protocolClient)
        it("should respond with failure for v2") {
          val response = onTrackPollController.onTrackV2(context.messageId)
          response.body?.get(0)?.error?.code shouldBe "BAP_007"
        }
      }

    }
  }

  fun trackResults(): List<ProtocolOnTrack> {
    return listOf(
      protocolOnTrack,
      protocolOnTrack
    )
  }
}