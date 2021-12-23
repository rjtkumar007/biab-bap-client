package org.beckn.one.sandbox.bap.client.accounts.address.controllers

import arrow.core.Either
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.accounts.address.services.AddressServices
import org.beckn.one.sandbox.bap.client.shared.dtos.BillingDetailsResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressRequestDto
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressResponse
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

internal class OnAddressControllerTest @Autowired constructor(): DescribeSpec() {
  private var addressServices = Mockito.mock(AddressServices::class.java)
  init {
    describe("AddressController test cases"){
      it("UNAUTHORIZED error on no user in context"){
        val authentication: Authentication = Mockito.mock(Authentication::class.java)
        val securityContext: SecurityContext = Mockito.mock(SecurityContext::class.java)
        SecurityContextHolder.setContext(securityContext)
        Mockito.`when`(securityContext.authentication).thenReturn(authentication)
        Mockito.`when`(securityContext.authentication.isAuthenticated).thenReturn(true)
        invokeOnAddressDetails().statusCode shouldBe HttpStatus.UNAUTHORIZED
      }

      it("Should return list of DeliveryAddressResponse when invoked with current user"){
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
        Mockito.`when`(addressServices.findAddressesForCurrentUser(any())).thenReturn(listOfBillingDetailsResponse())
        invokeOnAddressDetails().statusCode shouldBe HttpStatus.OK
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

  private fun listOfBillingDetailsResponse(): ResponseEntity<List<DeliveryAddressResponse>>? {
    var listOfDeliveryAddressResponse = ArrayList<DeliveryAddressResponse>()
    return ResponseEntity.ok(listOfDeliveryAddressResponse)
  }

  private fun invokeOnAddressDetails(): ResponseEntity<out List<DeliveryAddressResponse>> {
    var addressRequestDto = DeliveryAddressRequestDto(
      descriptor = null,
      gps = "test",
      default = false,
      address = null
    )
    var addressController = OnAddressController(
      addressService = addressServices
    )
    return addressController.onSearchAddress();
  }
}
