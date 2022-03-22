package org.beckn.one.sandbox.bap.client.order.status.controllers

import arrow.core.Either
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.order.status.services.OnOrderStatusService
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderStatusResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientQuoteResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.common.factories.MockProtocolBap
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.message.factories.ProtocolOrderFactory
import org.beckn.one.sandbox.bap.message.mappers.OnOrderProtocolToEntityOrder
import org.beckn.protocol.schemas.ProtocolOnOrderStatus
import org.beckn.protocol.schemas.ProtocolOnOrderStatusMessage
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
internal class OnOrderStatusPollControllerSpec @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val mapper: ObjectMapper,
  val mapping: OnOrderProtocolToEntityOrder,
  private val protocolClient: ProtocolClient,
  private val mockMvc: MockMvc,
  private val onOrderStatusService: OnOrderStatusService
) : DescribeSpec() {

  val context = contextFactory.create()
  private val protocolOnOrderStatus = ProtocolOnOrderStatus(
    context = context,
    message = ProtocolOnOrderStatusMessage(
      order = ProtocolOrderFactory.create(1)
    )
  )
  val mockProtocolBap = MockProtocolBap.withResetInstance()

  init {
    describe("OnOrderStatus callback") {

      context("when called for given order id") {
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v1/on_status?orderId=1001")
            .willReturn(WireMock.okJson(mapper.writeValueAsString(orderStatusResults())))
        )
        mockProtocolBap.stubFor(
          WireMock.get("/protocol/response/v1/on_order_status?orderId=1001")
            .willReturn(WireMock.okJson(mapper.writeValueAsString(orderStatusResults())))
        )
        val onOrderStatusCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v1/on_order_status")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("orderId", "1001")
          )

        it("should respond with status ok") {
          onOrderStatusCall.andExpect(status().isOk)
        }

        it("should respond with all order status responses in body") {
          val results = onOrderStatusCall.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, ClientOrderStatusResponse::class.java)
          clientResponse.message?.order shouldNotBe null
          clientResponse.message?.order shouldBe protocolOnOrderStatus.message?.order
        }
      }

      context("when failure occurs during request processing") {
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnOrderStatus, ClientOrderStatusResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnRead))
        }
        val onOrderStatusPollController = OnOrderStatusPollController(mockOnPollService, contextFactory,mapping,
          protocolClient,onOrderStatusService)
        it("should respond with failure") {
          val response = onOrderStatusPollController.onOrderStatusV1("1001")
          response.statusCode shouldBe DatabaseError.OnRead.status()
        }
      }

      context("when called for empty message ids of v2 order status") {
        val onOrderStatusCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v2/on_order_status")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("orderIds", "")
          )

        it("should respond with bad error for v2 on Order status api") {
          onOrderStatusCall.andExpect(status().is4xxClientError)
        }

        it("should respond with v2 on Order status api responses in body") {
          val results = onOrderStatusCall.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, object : TypeReference<List<ClientQuoteResponse>>(){})
          clientResponse.first().error shouldNotBe null
          clientResponse.first().error?.message shouldBe BppError.BadRequestError.error().message
        }
      }
      context("when called unauthorized user for message ids should return error ") {
        val authentication: Authentication = Mockito.mock(Authentication::class.java)
        val securityContext: SecurityContext = Mockito.mock(SecurityContext::class.java)
        SecurityContextHolder.setContext(securityContext)
        Mockito.`when`(securityContext.authentication).thenReturn(authentication)
        Mockito.`when`(securityContext.authentication.isAuthenticated).thenReturn(true)
        Mockito.`when`(securityContext.authentication.principal).thenReturn(
        null
        )
        val mockPollResponse = ClientOrderStatusResponse(message =protocolOnOrderStatus.message,context = protocolOnOrderStatus.context!! )

        val mockOnPollService = mock<GenericOnPollService<ProtocolOnOrderStatus, ClientOrderStatusResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(Either.Right(mockPollResponse))
        }
        val onOrderServiceSuccess = mock<OnOrderStatusService> {
          onGeneric { updateOrder(any()) }.thenReturn(Either.Left(DatabaseError.OnWrite))
        }
        val onOrderStatusPollController = OnOrderStatusPollController(mockOnPollService, contextFactory,mapping,
          protocolClient,onOrderServiceSuccess)

        val response = onOrderStatusPollController.onOrderStatusV2("1001")
        response.body?.get(0)?.error shouldNotBe null
        response.body?.get(0)?.error?.code shouldNotBe null
        response.body?.get(0)?.error shouldBe BppError.AuthenticationError.autheticationError
      }

      context("when called for  message ids should return error on update") {
        setMockAuthentication()
        it("should respond with v2 on Order status api failure") {
          val onOrderServiceSuccess = mock<OnOrderStatusService> {
            onGeneric { updateOrder(any()) }.thenReturn(Either.Left(DatabaseError.OnWrite))
          }
          val mockPollResponse = ClientOrderStatusResponse(message =protocolOnOrderStatus.message,context = protocolOnOrderStatus.context!! )

          val mockOnPollService = mock<GenericOnPollService<ProtocolOnOrderStatus, ClientOrderStatusResponse>> {
            onGeneric { onPoll(any(), any()) }.thenReturn(Either.Right(mockPollResponse))
          }

          val onOrderStatusPollController = OnOrderStatusPollController(mockOnPollService, contextFactory,mapping,
            protocolClient,onOrderServiceSuccess)

          val response = onOrderStatusPollController.onOrderStatusV2("1001")
          response.body?.get(0)?.error shouldNotBe null
          response.body?.get(0)?.error?.code shouldNotBe null
        }

      }

      context("when called for message ids of v2 order status for authorized user ") {
        setMockAuthentication()
        val onOrderStatusCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v2/on_order_status")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("orderIds", "1001")
          )
        it("should respond with successful for v2 on Order status api") {
          onOrderStatusCall.andExpect(status().is2xxSuccessful)
        }
        it("should respond with v2 on Order status api responses in body") {
          val results = onOrderStatusCall.andReturn()
          val body = results.response.contentAsString
          val clientResponse = mapper.readValue(body, object : TypeReference<List<ClientOrderStatusResponse>>(){})
          clientResponse.first().message?.order shouldNotBe null
          clientResponse.first().message?.order shouldBe protocolOnOrderStatus.message?.order
        }
      }
      context("should respond error on polling for v2 order status") {
        setMockAuthentication()
        val mockOnPollService = mock<GenericOnPollService<ProtocolOnOrderStatus, ClientOrderStatusResponse>> {
          onGeneric { onPoll(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnRead))
        }
        val onOrderStatusPollController = OnOrderStatusPollController(mockOnPollService, contextFactory,mapping,
          protocolClient,onOrderStatusService)
        it("should respond with failure for v2") {
          val response = onOrderStatusPollController.onOrderStatusV2("1001")
          response.statusCode shouldBe HttpStatus.OK
          response.body?.get(0)?.error shouldNotBe null
          response.body?.get(0)?.error?.code shouldNotBe null
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
  fun orderStatusResults(): List<ProtocolOnOrderStatus> {
    return listOf(
      protocolOnOrderStatus,
      protocolOnOrderStatus
    )
  }
}