package org.beckn.one.sandbox.bap.client.order.quote.controllers

import arrow.core.Either
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.order.init.controllers.OnInitOrderController
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientInitResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientQuoteResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.common.factories.MockProtocolBap
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.message.factories.ProtocolOnSelectMessageSelectedFactory
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolOnInit
import org.beckn.protocol.schemas.ProtocolOnSelect
import org.beckn.protocol.schemas.ProtocolOnSelectMessage
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
internal class OnGetQuotePollControllerSpec @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val mapper: ObjectMapper,
  private val protocolClient: ProtocolClient,
  private val mockMvc: MockMvc
) : DescribeSpec() {
  private val fixedClock = Clock.fixed(
    Instant.parse("2018-11-30T18:35:24.00Z"),
    ZoneId.of("UTC")
  )

  val context = contextFactory.create()
  private val protocolOnSelect = ProtocolOnSelect(
    context,
    message = ProtocolOnSelectMessage(order = ProtocolOnSelectMessageSelectedFactory.create())
  )
  val mockProtocolBap = MockProtocolBap.withResetInstance()

  init {
    describe("OnGetQuote callback") {

      context("when called for given message id") {
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v1/on_select?messageId=${context.messageId}")
            .willReturn(WireMock.okJson(mapper.writeValueAsString(entitySelectResults())))
        )
        val onGetQuoteCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v1/on_get_quote")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageId", context.messageId)
          )

        it("should respond with status ok") {
          onGetQuoteCall.andExpect(status().isOk)
        }

        it("should respond with all select responses in body") {
          val results = onGetQuoteCall.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, ClientQuoteResponse::class.java)
          clientResponse.message?.quote shouldNotBe null
          clientResponse.message?.quote?.quote shouldBe protocolOnSelect.message?.order?.quote
        }
      }

      context("when failure occurs during request processing") {
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnSelect, ClientQuoteResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnRead))
        }
        val onSelectPollController = OnGetQuotePollController(mockOnPollService, contextFactory, protocolClient)
        it("should respond with failure") {
          val response = onSelectPollController.onGetQuoteV1(context.messageId)
          response.statusCode shouldBe DatabaseError.OnRead.status()
        }
      }

      context("when called for empty message ids of v2 quotes") {
        val onGetQuoteCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v2/on_get_quote")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageIds", "")
          )

        it("should respond with bad error") {
          onGetQuoteCall.andExpect(status().is4xxClientError)
        }

        it("should respond with all select responses in body") {
          val results = onGetQuoteCall.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, object : TypeReference<List<ClientQuoteResponse>>(){})
          clientResponse.first().error shouldNotBe null
          clientResponse.first().error?.message shouldBe BppError.BadRequestError.error().message
        }
      }

      context("when called for message ids of v2 quotes") {
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v1/on_select?messageId=${context.messageId}")
            .willReturn(WireMock.okJson(mapper.writeValueAsString(entitySelectResults())))
        )
        val onGetQuoteCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v2/on_get_quote")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageIds", context.messageId)
          )

        it("should respond with status ok") {
          onGetQuoteCall.andExpect(status().isOk)
        }

        it("should respond with all select responses in body") {
          val results = onGetQuoteCall.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, object : TypeReference<List<ClientQuoteResponse>>(){})
          clientResponse.first().message?.quote shouldNotBe null
          clientResponse.first().message?.quote?.quote shouldBe protocolOnSelect.message?.order?.quote
        }
      }

      context("when failure occurs during request processing on quotes v2") {
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnSelect, ClientQuoteResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnRead))
        }
        val onInitPollController = OnGetQuotePollController(mockOnPollService, contextFactory, protocolClient)
        it("should respond with failure") {
          val response = onInitPollController.onGetQuoteV2(context.messageId)
          val responseMessage = response.body

          responseMessage?.first()?.error shouldNotBe null
          responseMessage?.first()?.error shouldBe DatabaseError.OnRead.error()
          response.statusCode shouldBe HttpStatus.OK
        }
      }


    }
  }

  fun entitySelectResults(): List<ProtocolOnSelect> {
    return listOf(
      protocolOnSelect,
      protocolOnSelect
    )
  }
}