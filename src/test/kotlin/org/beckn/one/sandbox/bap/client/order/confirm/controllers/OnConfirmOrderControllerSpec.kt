package org.beckn.one.sandbox.bap.client.order.confirm.controllers

import arrow.core.Either
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.order.confirm.services.ConfirmOrderService
import org.beckn.one.sandbox.bap.client.order.confirm.services.OnConfirmOrderService
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientConfirmResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientQuoteResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.common.factories.MockProtocolBap
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.factories.ProtocolOrderFactory
import org.beckn.one.sandbox.bap.message.mappers.OnOrderProtocolToEntityOrder
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolOnConfirm
import org.beckn.protocol.schemas.ProtocolOnConfirmMessage
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.rmi.ServerError

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
internal class OnConfirmOrderControllerSpec @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val mapper: ObjectMapper,
  private val protocolClient: ProtocolClient,
  private val mockMvc: MockMvc,
  private val mapping: OnOrderProtocolToEntityOrder,
  private val onConfirmOrderService: OnConfirmOrderService
) : DescribeSpec() {
  val context = contextFactory.create()
  private val protocolOnConfirm = ProtocolOnConfirm(
    context,
    message = ProtocolOnConfirmMessage(
      order = ProtocolOrderFactory.create(1)
    )
  )
  val mockProtocolBap = MockProtocolBap.withResetInstance()

  init {
    describe("OnConfirm callback") {

      context("when called for given message id") {
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v1/on_confirm?messageId=${context.messageId}")
            .willReturn(WireMock.okJson(mapper.writeValueAsString(entityOnConfirmResults())))
        )
        val onConfirmCallBack = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v1/on_confirm_order")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageId", context.messageId)
          )

        it("should respond with status ok") {
          onConfirmCallBack.andExpect(MockMvcResultMatchers.status().isOk)
        }

        it("should respond with all on confirm responses in body") {
          val results = onConfirmCallBack.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, ClientConfirmResponse::class.java)
          clientResponse.message shouldNotBe null
        }
      }

      context("when failure occurs during request processing") {
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnConfirm, ClientConfirmResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnRead))
        }
        val onConfirmPollController =
          OnConfirmOrderController(mockOnPollService, contextFactory, protocolClient, mapping, onConfirmOrderService)
        it("should respond with failure") {
          val response = onConfirmPollController.onConfirmOrderV1(context.messageId)
          response.statusCode shouldBe DatabaseError.OnRead.status()
        }
      }

      context("when user is not authenticated") {
        val authentication: Authentication = Mockito.mock(Authentication::class.java)
        val securityContext: SecurityContext = Mockito.mock(SecurityContext::class.java)
        SecurityContextHolder.setContext(securityContext)
        Mockito.`when`(securityContext.authentication).thenReturn(authentication)
        Mockito.`when`(securityContext.authentication.isAuthenticated).thenReturn(true)
        Mockito.`when`(securityContext.authentication.principal).thenReturn(null)

        val onConfirmCallBack = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v2/on_confirm_order")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageIds", "")
          )
        it("should respond with status UnAuthorized") {
          onConfirmCallBack.andExpect(MockMvcResultMatchers.status().isUnauthorized)
        }
        it("should respond with UnAuthorized and error message") {
          val results = onConfirmCallBack.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, object : TypeReference<List<ClientConfirmResponse>>() {})
          clientResponse.first().error shouldNotBe null
          clientResponse.first().error?.code shouldNotBe null
          clientResponse.first().error shouldBe BppError.AuthenticationError.autheticationError
        }
      }


      context("when user is authenticated and messageIds is empty") {
        setMockAuthentication()
        val onConfirmCallBack = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v2/on_confirm_order")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageIds", "")
          )
        it("should respond with error when messageIds empty") {
          onConfirmCallBack.andExpect(MockMvcResultMatchers.status().is4xxClientError)
        }
        it("should respond with error code ") {
          val results = onConfirmCallBack.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, object : TypeReference<List<ClientConfirmResponse>>() {})
          clientResponse.first().error shouldNotBe null
          clientResponse.first().error?.code shouldNotBe null
          clientResponse.first().error shouldBe BppError.BadRequestError.badRequestError
        }
      }

      context("when user is authenticated and messageIds is invalid and network call fails") {
        setMockAuthentication()
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v1/on_confirm?messageId=${context.messageId}")
            .willReturn(WireMock.serverError())
        )
        val onConfirmCallBack = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v2/on_confirm_order")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageIds", context.messageId)
          )
        it("should respond with error when messageIds is Invalid from netwrok") {
          onConfirmCallBack.andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
        }
        it("should respond with error code when network call fails ") {
          val results = onConfirmCallBack.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, object : TypeReference<List<ClientConfirmResponse>>() {})
          clientResponse.first().error shouldNotBe null
          clientResponse.first().error?.code shouldNotBe null
          clientResponse.first().context shouldNotBe null
          clientResponse.first().error?.message shouldNotBe null
        }
      }

      context("when user is authenticated and messageIds is valid and network call success") {
        setMockAuthentication()
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v1/on_confirm?messageId=${context.messageId}")
            .willReturn(WireMock.okJson(mapper.writeValueAsString(entityOnConfirmResults())))
        )
        val onConfirmCallBack = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v2/on_confirm_order")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("messageIds", context.messageId)
          )
        it("should respond with error when messageIds is Invalid from netwrok") {
          onConfirmCallBack.andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
        }
        it("should respond with error code when network call fails ") {
          val results = onConfirmCallBack.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, object : TypeReference<List<ClientConfirmResponse>>() {})
          clientResponse.first().context shouldNotBe null
          clientResponse.first().error shouldNotBe  null
        }
      }
      context("when network call success but db update fails") {
        setMockAuthentication()
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnConfirm, ClientConfirmResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(
            Either.Right(ClientConfirmResponse(
              contextFactory.create(),message = entityOnConfirmResults().first().message,error = null
            )))
        }
        val onConfirmServiceProvider = mock<OnConfirmOrderService> {
          onGeneric { updateOrder(any()) }.thenReturn(
            Either.Left(DatabaseError.OnWrite))

          onGeneric { findById(any()) }.thenReturn(
            Either.Left(DatabaseError.OnWrite))

        }

        val onConfirmPollController =
          OnConfirmOrderController(mockOnPollService, contextFactory, protocolClient, mapping, onConfirmServiceProvider)
        val onConfirmCallBack = onConfirmPollController.onConfirmOrderV2(context.messageId)

        it("should respond with error code when update fails ") {
          val clientResponse = onConfirmCallBack.body as List<ClientConfirmResponse>
          clientResponse?.first()?.context shouldNotBe null
          clientResponse?.first()?.error shouldBe DatabaseError.OnWrite.onWriteError
        }
      }

      context("when network call success returns no data") {
        setMockAuthentication()
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnConfirm, ClientConfirmResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(
            Either.Right(ClientConfirmResponse(
              contextFactory.create(),message = ProtocolOnConfirmMessage(order = null)
            )))
        }

        val onConfirmServiceProvider = mock<OnConfirmOrderService> {
          onGeneric { updateOrder(any()) }.thenReturn(
            Either.Left(DatabaseError.OnWrite))

          onGeneric { findById(any()) }.thenReturn(
            Either.Left(DatabaseError.OnWrite))

        }

        val onConfirmPollController =
          OnConfirmOrderController(mockOnPollService, contextFactory, protocolClient, mapping, onConfirmServiceProvider)
        val onConfirmCallBack = onConfirmPollController.onConfirmOrderV2(context.messageId)

        it("should respond with error code when return no data ") {
          val clientResponse = onConfirmCallBack.body as List<ClientConfirmResponse>
          clientResponse?.first()?.context shouldNotBe null
          clientResponse?.first()?.error shouldBe DatabaseError.NoDataFound.noDataFoundError
        }
      }
    }
  }
  private  fun setMockAuthentication(){
    val authentication: Authentication = Mockito.mock(Authentication::class.java)
    val securityContext: SecurityContext = Mockito.mock(SecurityContext::class.java)
    SecurityContextHolder.setContext(securityContext)
    Mockito.`when`(securityContext.authentication).thenReturn(authentication)
    Mockito.`when`(securityContext.authentication.isAuthenticated).thenReturn(true)
    Mockito.`when`(securityContext.authentication.principal).thenReturn(
      User(
        uid = "1234533434343",
        name = "John",
        email = "john@gmail.com",
        isEmailVerified = true
      )
    )
  }

  fun entityOnConfirmResults(): List<ProtocolOnConfirm> {
    return listOf(
      protocolOnConfirm,
      protocolOnConfirm
    )
  }
}