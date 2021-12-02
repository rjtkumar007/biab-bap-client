package org.beckn.one.sandbox.bap.client.accounts.user.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.accounts.user.services.AccountDetailsServices
import org.beckn.one.sandbox.bap.client.shared.dtos.AccountDetailsResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.AccountRequestDto
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
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
internal class OnAccountDetailsControllerTest @Autowired constructor(
  private val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val contextFactory: ContextFactory
): DescribeSpec() {
  @MockBean
  private lateinit var accountDetailsServices: AccountDetailsServices

  init {
    describe("on_account_details test"){
      val authentication: Authentication = Mockito.mock(Authentication::class.java)
      val securityContext: SecurityContext = Mockito.mock(SecurityContext::class.java)
      SecurityContextHolder.setContext(securityContext)
      Mockito.`when`(securityContext.authentication).thenReturn(authentication)
      Mockito.`when`(securityContext.authentication.isAuthenticated).thenReturn(true)

      it("Fail to execute account details services without a current user") {
        invokeOnAccountDetails().andExpect(MockMvcResultMatchers.status().is4xxClientError)
      }

      it("Should work when given an authorized user") {
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
        Mockito.`when`(securityContext.authentication.principal).thenReturn(
          User(
            uid = "1234533434343",
            name = "John",
            email = "john@gmail.com",
            isEmailVerified = true
          )
        )
        Mockito.`when`(accountDetailsServices.updateAccountDetails(any(), any())).thenReturn(accountDetailsResponse(userName = "Khanna",userEmail = "simplyrajat010@gmail.com",userPhone = "9898928272"))
        invokeOnAccountDetails().andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
      }
    }
  }
  private  fun accountDetailsResponse(userName: String = "", userPhone: String = "", userEmail: String = ""): ResponseEntity<AccountDetailsResponse> {
    val accountDetailsResponse = AccountDetailsResponse(
      context = null,
      name = userName,
      error = null,
      userId = null,
      email = userEmail,
      phone = userPhone
    )
    return ResponseEntity
      .status(HttpStatus.OK)
      .body(
        accountDetailsResponse
      )
  }
  private fun invokeOnAccountDetails(): ResultActions {
    return mockMvc
      .perform(
        MockMvcRequestBuilders.get("/client/v1/account_details")
          .contentType(MediaType.APPLICATION_JSON)
      )
  }
}
