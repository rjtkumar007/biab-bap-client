package org.beckn.one.sandbox.bap.client.order.orderhistory.controllers

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.order.orderhistory.services.OrderServices
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderResponse
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class OnOrderHistoryControllerSpec @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val mapper: ObjectMapper,
  private val mockMvc: MockMvc,


):DescribeSpec(){
  val context = contextFactory.create()
  init {
    describe("On Order callback") {

      context("when return error on orderList invoke for specific orderId") {
        setMockAuthentication()
        val onGetOrdersCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v1/orders")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("orderId", "121212")
          )

        it("should respond with no data found") {
          onGetOrdersCall.andExpect(MockMvcResultMatchers.status().isNotFound)
        }
      }
      context("return success on orderList invoke for specific orderId") {
        setMockAuthentication()
        val orderDao = OrderDao(messageId = "23232323232",userId = "james")
        val mockOrderServices= mock<OrderServices> {
          onGeneric { findAllOrders(any(),any(),any(), any(), any()) }.thenReturn(Either.Right(listOf(OrderResponse(
            messageId = "12121",userId = null,error =  null, context = context
          ))))
        }
        val onOrdersController = OnOrderHistoryController(mockOrderServices)
        val response = onOrdersController.onOrdersList("111","1",0,1)
        it("should respond with success") {
          val responseMessage = response.body
          responseMessage?.first()?.error.shouldBe(null)
          response.statusCode shouldBe HttpStatus.OK
        }
      }
      context("when user is not authorized to access api") {
        val authentication: Authentication = Mockito.mock(Authentication::class.java)
        val securityContext: SecurityContext = Mockito.mock(SecurityContext::class.java)
        SecurityContextHolder.setContext(securityContext)
        Mockito.`when`(securityContext.authentication).thenReturn(authentication)
        Mockito.`when`(securityContext.authentication.isAuthenticated).thenReturn(true)
        Mockito.`when`(securityContext.authentication.principal).thenReturn(null)

          val onGetOrdersCall = mockMvc
          .perform(
            MockMvcRequestBuilders.get("/client/v1/orders")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .param("orderId", "121212")
          )

        it("should respond with no data found") {
          onGetOrdersCall.andExpect(MockMvcResultMatchers.status().isUnauthorized)
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
}