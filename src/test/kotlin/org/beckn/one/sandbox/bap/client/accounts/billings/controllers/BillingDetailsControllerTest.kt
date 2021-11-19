package org.beckn.one.sandbox.bap.client.accounts.billings.controllers

import arrow.core.Either
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.shared.dtos.BillingDetailRequestDto
import org.beckn.one.sandbox.bap.client.shared.dtos.BillingDetailsResponse
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.BillingDetailsDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.junit.Assert
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

internal class BillingDetailsControllerTest : DescribeSpec() {

  private val responseStorageService = mock<ResponseStorageService<BillingDetailsResponse, BillingDetailsDao>> {
    onGeneric { save(any()) }.thenReturn(Either.Right(billingDetailsResponse("test","1234567890")))
  }
  private val responseStorageServiceError = mock<ResponseStorageService<BillingDetailsResponse, BillingDetailsDao>> {
    onGeneric { save(any()) }.thenReturn(Either.Left(DatabaseError.OnWrite))
  }
  init {
    describe("BillingDetailsController test cases"){
      it("UNAUTHORIZED error on no user in context"){
        val authentication: Authentication = Mockito.mock(Authentication::class.java)
        val securityContext: SecurityContext = Mockito.mock(SecurityContext::class.java)
        SecurityContextHolder.setContext(securityContext)
        Mockito.`when`(securityContext.authentication).thenReturn(authentication)
        Mockito.`when`(securityContext.authentication.isAuthenticated).thenReturn(true)
        var response = invokeBillingDetails("test","1234567890")//.andExpect(MockMvcResultMatchers.status().is4xxClientError)
        response.statusCode shouldBe HttpStatus.UNAUTHORIZED
      }
      it("Returns error when saving into Db and returns billing dto with null values"){
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
        var response = invokeBillingDetailsError("test","1234567890")//.andExpect(MockMvcResultMatchers.status().is4xxClientError)
        response.statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
      }
      it("Save billing details and return billing details response with http status OK"){
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
        var response = invokeBillingDetails("test","1234567890")//.andExpect(MockMvcResultMatchers.status().is4xxClientError)
        response.statusCode shouldBe HttpStatus.OK
        Assert.assertEquals(response.body?.name,"test")
      }
    }
  }

  private fun invokeBillingDetails(name: String = "", phone: String = ""): ResponseEntity<BillingDetailsResponse> {

    val billingDetailrReqDto = BillingDetailRequestDto(
      name = name,
      phone = phone
    );
    var billingDetailsController = BillingDetailsController(
      responseStorageService = responseStorageService
    )
    return billingDetailsController.deliveryAddress(billingDetailrReqDto);
  }

  private fun invokeBillingDetailsError(name: String = "", phone: String = ""): ResponseEntity<BillingDetailsResponse> {

    val billingDetailrReqDto = BillingDetailRequestDto(
      name = name,
      phone = phone
    );
    var billingDetailsController = BillingDetailsController(
      responseStorageService = responseStorageServiceError
    )
    return billingDetailsController.deliveryAddress(billingDetailrReqDto);
  }

  private  fun billingDetailsResponse(name: String = "", phone: String = ""): BillingDetailsResponse {
    val billingDetailsResponse = BillingDetailsResponse(
      name = name,
      phone = phone,
      address = null,
      context = null,
      createdAt = null,
      email = null,
      error = null,
      id = null,
      locationId = null,
      organization = null,
      taxNumber = null,
      time = null,
      updatedAt = null,
      userId = null
    )
    return billingDetailsResponse
  }
}