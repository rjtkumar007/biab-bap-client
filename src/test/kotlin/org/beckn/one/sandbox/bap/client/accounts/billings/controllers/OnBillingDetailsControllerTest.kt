package org.beckn.one.sandbox.bap.client.accounts.billings.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.accounts.billings.services.BillingDetailService
import org.beckn.one.sandbox.bap.client.shared.dtos.BillingDetailRequestDto
import org.beckn.one.sandbox.bap.client.shared.dtos.BillingDetailsResponse
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
internal class OnBillingDetailsControllerTest @Autowired constructor(
  private val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val contextFactory: ContextFactory
): DescribeSpec() {
  @MockBean
  private lateinit var billingService: BillingDetailService
  init {
    describe("OnBillingDetailsController test cases"){
      it("UNAUTHORIZED error on no user in context"){
        val authentication: Authentication = Mockito.mock(Authentication::class.java)
        val securityContext: SecurityContext = Mockito.mock(SecurityContext::class.java)
        SecurityContextHolder.setContext(securityContext)
        Mockito.`when`(securityContext.authentication).thenReturn(authentication)
        Mockito.`when`(securityContext.authentication.isAuthenticated).thenReturn(true)
        invokeOnBillingDetails("test","1234567890").andExpect(MockMvcResultMatchers.status().is4xxClientError)
      }
      it("Should return list of BillingDetailsResponse when invoked with current user"){
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
        Mockito.`when`(billingService.findBillingsForCurrentUser(any())).thenReturn(listOfBillingDetailsResponse())
        invokeOnBillingDetails("test","1234567890").andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
      }
    }
  }

  private fun listOfBillingDetailsResponse(): ResponseEntity<List<BillingDetailsResponse>>? {
    var listOfBillings = ArrayList<BillingDetailsResponse>()
    return ResponseEntity.ok(listOfBillings)
  }

  private fun invokeOnBillingDetails(name: String = "", phone: String = ""): ResultActions {

    val billingDetailrReqDto = BillingDetailRequestDto(
      name = name,
      phone = phone
    );
    return mockMvc
      .perform(
        MockMvcRequestBuilders.get("/client/v1/billing_details")
          .contentType(MediaType.APPLICATION_JSON)
      )
  }
}