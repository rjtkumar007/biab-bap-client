package org.beckn.one.sandbox.bap.client.accounts.user.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.accounts.user.services.AccountDetailsServices
import org.beckn.one.sandbox.bap.client.external.provider.BppClientFactory
import org.beckn.one.sandbox.bap.client.shared.dtos.*
import org.beckn.one.sandbox.bap.common.factories.MockNetwork
import org.beckn.one.sandbox.bap.common.factories.ResponseFactory
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.protocol.schemas.ProtocolError
import org.hamcrest.CoreMatchers
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
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
internal class AccountDetailsControllerTest @Autowired constructor(
  private val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val contextFactory: ContextFactory
): DescribeSpec() {

  @MockBean
  private lateinit var accountDetailsServices: AccountDetailsServices

  init {
    describe("account_details test"){

      /*MockNetwork.startAllSubscribers()
      beforeEach {
        MockNetwork.resetAllSubscribers()
      }*/

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

      it("should invoke Beckn account details API on specified BPP using gateway and persist account details") {
        Mockito.`when`(accountDetailsServices.updateAccountDetails(any(), any())).thenReturn(accountDetailsResponse(userName = "Khanna",userEmail = "simplyrajat010@gmail.com",userPhone = "9898928272"))
        //Mockito.`when`(accountDetailsServices.findAccountDetailForCurrentUser(any())).thenReturn(accountDetailsResponse(userName = "Khanna",userEmail = "simplyrajat010@gmail.com",userPhone = "9898928272"))
        invokeAccountDetails(userName = "Khanna",userEmail = "simplyrajat010@gmail.com",userPhone = "9898928272")

      }
    }
  }

  private  fun accountDetailsResponse(userName: String = "", userPhone: String = "", userEmail: String = ""):ResponseEntity<AccountDetailsResponse>{
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
  private fun invokeAccountDetails(userName: String = "", userPhone: String = "", userEmail: String = "") {

    val accountRequestDto = AccountRequestDto(
      userName = userName,
      userPhone = userPhone,
      userEmail = userEmail
    );
     mockMvc
      .perform(
        MockMvcRequestBuilders.post("/client/v1/account_details")
          .content(objectMapper.writeValueAsString(accountRequestDto))
          .contentType(MediaType.APPLICATION_JSON)
      ).andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
  }
}