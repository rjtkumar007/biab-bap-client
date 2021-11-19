package org.beckn.one.sandbox.bap.client.accounts.address.controllers

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.accounts.address.services.AddressServices
import org.beckn.one.sandbox.bap.client.accounts.billings.controllers.BillingDetailsController
import org.beckn.one.sandbox.bap.client.shared.dtos.BillingDetailsResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressRequestDto
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressResponse
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
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
import org.testcontainers.shaded.okhttp3.Address

internal class AddressControllerTest @Autowired constructor(): DescribeSpec() {
  private var addressServices = Mockito.mock(AddressServices::class.java)
  init {
      describe("AddressController test cases"){
        it("UNAUTHORIZED error on no user in context"){
          val authentication: Authentication = Mockito.mock(Authentication::class.java)
          val securityContext: SecurityContext = Mockito.mock(SecurityContext::class.java)
          SecurityContextHolder.setContext(securityContext)
          Mockito.`when`(securityContext.authentication).thenReturn(authentication)
          Mockito.`when`(securityContext.authentication.isAuthenticated).thenReturn(true)
          invokeAddressDetails().statusCode shouldBe HttpStatus.UNAUTHORIZED
        }
        it("Should return http error when invoked with current user but fais to save into DB"){
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
          Mockito.`when`(addressServices.updateAndSaveAddress(any())).thenReturn(Either.Left(DatabaseError.OnWrite))
          invokeAddressDetails().statusCode shouldBe HttpStatus.INTERNAL_SERVER_ERROR
        }
        it("Should return DeliveryAddressResponse when invoked with current user"){
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
          Mockito.`when`(addressServices.updateAndSaveAddress(any())).thenReturn(Either.Right(deliveryAddressResponse()))
          invokeAddressDetails().statusCode shouldBe HttpStatus.OK
        }
      }
  }

  private fun deliveryAddressResponse(): DeliveryAddressResponse {
    return DeliveryAddressResponse(
      id = "",
      context = null,
      userId = "1234533434343",
      error = null,
      gps = "test"
    )
  }

  private fun invokeAddressDetails(): ResponseEntity<DeliveryAddressResponse> {
    var addressRequestDto = DeliveryAddressRequestDto(
      descriptor = null,
      gps = "test",
      default = false,
      address = null
    )
    var addressController = AddressController(
      addressServices = addressServices
    )
    return addressController.deliveryAddress(addressRequestDto);
  }
}