package org.beckn.one.sandbox.bap.client.order.init.controllers

import arrow.core.Either
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientInitResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.common.factories.MockProtocolBap
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.message.factories.ProtocolOnInitMessageInitializedFactory
import org.beckn.protocol.schemas.ProtocolOnInit
import org.beckn.protocol.schemas.ProtocolOnInitMessage
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
internal class OnInitOrderControllerSpec @Autowired constructor(
    private val contextFactory: ContextFactory,
    private val mapper: ObjectMapper,
    private val protocolClient: ProtocolClient,
    private val mockMvc: MockMvc
) : DescribeSpec() {
  val context = contextFactory.create()
  private val protocolOnInit = ProtocolOnInit(
    context,
    message = ProtocolOnInitMessage(ProtocolOnInitMessageInitializedFactory.create(id = 1, numberOfItems = 1))
  )

  val mockProtocolBap = MockProtocolBap.withResetInstance()
  init {
    describe("OnInitialize callback") {

      context("when called for given message id") {
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v1/on_init?messageId=${context.messageId}")
            .willReturn(WireMock.okJson(mapper.writeValueAsString(entityOnInitResults())))
        )
        val onInitCallBack = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v1/on_initialize_order")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageId", context.messageId)
          )

        it("should respond with status ok") {
          onInitCallBack.andExpect(MockMvcResultMatchers.status().isOk)
        }

        it("should respond with all on init responses in body") {
          val results = onInitCallBack.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, ClientInitResponse::class.java)
          clientResponse.message shouldNotBe null
        }
      }

      context("when failure occurs during request processing") {
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnInit, ClientInitResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnRead))
        }
        val onInitPollController = OnInitOrderController(mockOnPollService, contextFactory, protocolClient)
        it("should respond with failure") {
          val response = onInitPollController.onInitOrderV1(context.messageId)
          response.statusCode shouldBe DatabaseError.OnRead.status()
        }
      }

      context("when called for empty message ids of v2 init") {
        val onGetQuoteCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v2/on_initialize_order")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageIds", "")
          )

        it("should respond with bad error") {
          onGetQuoteCall.andExpect(MockMvcResultMatchers.status().is4xxClientError)
        }

        it("should respond with all init responses in body") {
          val results = onGetQuoteCall.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, object : TypeReference<List<ClientInitResponse>>(){})
          clientResponse.first().error shouldNotBe null
          clientResponse.first().error?.message shouldBe BppError.BadRequestError.error().message
        }
      }

      context("when called for message ids of v2 init") {
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v1/on_init?messageId=${context.messageId}")
            .willReturn(WireMock.okJson(mapper.writeValueAsString(entityOnInitResults())))
        )
        val onGetQuoteCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v2/on_initialize_order")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageIds", context.messageId)
          )

        it("should respond with status ok") {
          onGetQuoteCall.andExpect(MockMvcResultMatchers.status().isOk)
        }

        it("should respond with all select responses in body") {
          val results = onGetQuoteCall.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, object : TypeReference<List<ClientInitResponse>>(){})
          clientResponse.first().message?.order shouldNotBe null
          clientResponse.first().message?.order?.quote shouldBe protocolOnInit.message?.order?.quote
        }
      }

      context("when failure occurs during request processing on init v2") {
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnInit, ClientInitResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnRead))
        }
        val onInitPollController = OnInitOrderController(mockOnPollService, contextFactory, protocolClient)
        it("should respond with failure") {
          val response = onInitPollController.onInitOrderV2(context.messageId)
          val responseMessage = response.body

          responseMessage?.first()?.error shouldNotBe null
          responseMessage?.first()?.error shouldBe DatabaseError.OnRead.error()
          response.statusCode shouldBe HttpStatus.OK
        }
      }

    }
  }

  fun entityOnInitResults(): List<ProtocolOnInit> {
    return listOf(
      protocolOnInit,
      protocolOnInit,
    )
  }
}