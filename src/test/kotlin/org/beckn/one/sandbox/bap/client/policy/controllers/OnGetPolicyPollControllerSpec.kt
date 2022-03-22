package org.beckn.one.sandbox.bap.client.policy.controllers

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyResponse
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.common.factories.MockProtocolBap
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.message.factories.ProtocolOnCancellationMessageFeedbackFactory
import org.beckn.protocol.schemas.ProtocolOnCancellationReasons
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
internal class OnGetPolicyPollControllerSpec @Autowired constructor(
    private val contextFactory: ContextFactory,
    private val mapper: ObjectMapper,
    private val protocolClient: ProtocolClient,
    private val mockMvc: MockMvc
) : DescribeSpec() {

  val context = contextFactory.create()
  private val protocolOnCancellationReasons = ProtocolOnCancellationReasons(
    context,
    message = ProtocolOnCancellationMessageFeedbackFactory.create()
  )
  val mockProtocolBap = MockProtocolBap.withResetInstance()

  init {
    describe("OnGetCancellationPolicy callback") {

      context("when called for given message id") {
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v1/on_cancellation_reasons?messageId=${context.messageId}")
            .willReturn(WireMock.okJson(mapper.writeValueAsString(cancellationResults())))
        )
        val onCancellationReasonCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v1/on_cancellation_reasons")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageId", context.messageId)
          )

        it("should respond with status ok") {
          onCancellationReasonCall.andExpect(status().isOk)
        }

        it("should respond with all cancellation reason responses in body") {
          val results = onCancellationReasonCall.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, ClientOrderPolicyResponse::class.java)
          clientResponse.message?.cancellationReasons shouldNotBe null
          clientResponse.message?.cancellationReasons shouldBe protocolOnCancellationReasons.message?.cancellationReasons
        }
      }

      context("when failure occurs during request processing") {
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnCancellationReasons, ClientOrderPolicyResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnRead))
        }
        val onGetPolicyPollController = OnGetPolicyPollController(mockOnPollService, contextFactory, protocolClient)
        it("should respond with failure") {
          val response = onGetPolicyPollController.onCancellationReasonsV1(context.messageId)
          response.statusCode shouldBe DatabaseError.OnRead.status()
        }
      }
    }
  }

  fun cancellationResults(): List<ProtocolOnCancellationReasons> {
    return listOf(
      protocolOnCancellationReasons,
      protocolOnCancellationReasons
    )
  }
}