package org.beckn.one.sandbox.bap.client.accounts.address.services

import arrow.core.Either
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressRequestDto
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressResponse
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.AddDeliveryAddressDao
import org.beckn.one.sandbox.bap.message.mappers.GenericResponseMapper
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

internal class AddressServicesTest : DescribeSpec() {

  private val addressRepository = mock<BecknResponseRepository<AddDeliveryAddressDao>> {
    onGeneric { updateManyById(any(), any()) }.thenReturn(null);
    onGeneric { insertOne(any()) }.thenReturn(null)
  }

  private val addressRepositoryError = mock<BecknResponseRepository<AddDeliveryAddressDao>> {
    onGeneric { updateManyById(any(), any()) }.thenThrow(RuntimeException::class.java)
    onGeneric { insertOne(any()) }.thenReturn(null)
  }
  private val addressRepositoryInsertError = mock<BecknResponseRepository<AddDeliveryAddressDao>> {
    onGeneric { updateManyById(any(), any()) }.thenReturn(null);
    onGeneric { insertOne(any()) }.thenThrow(RuntimeException::class.java)
  }
  private val mapper = mock<GenericResponseMapper<DeliveryAddressResponse, AddDeliveryAddressDao>> {
    onGeneric { entityToProtocol(any()) }.thenReturn(null)
  }
  private val responseStorageServiceNoData =
    mock<ResponseStorageService<DeliveryAddressResponse, AddDeliveryAddressDao>> {
      onGeneric { findManyByUserId(any(), any(), any()) }.thenReturn(Either.Left(DatabaseError.NoDataFound))
    }
  private val responseStorageService =
    mock<ResponseStorageService<DeliveryAddressResponse, AddDeliveryAddressDao>> {
      onGeneric { findManyByUserId(any(), any(), any()) }.thenReturn(Either.Right(ArrayList<DeliveryAddressResponse>()))
    }

  init {
    describe("AddressService test") {
      it("Saves or updates address properly") {
        var addressService = AddressServices(
          addressRepository = addressRepository,
          mapper = mapper,
          responseStorageService = responseStorageServiceNoData
        )
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
        var addressRequestDto = DeliveryAddressRequestDto(
          descriptor = null,
          gps = "test",
          default = false,
          address = null
        )
        addressService.updateAndSaveAddress(addressRequestDto)
      }

      it("Error handling for database error on save or update address"){
        var addressService = AddressServices(
          addressRepository = addressRepositoryError,
          mapper = mapper,
          responseStorageService = responseStorageServiceNoData
        )
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
        var addressRequestDto = DeliveryAddressRequestDto(
          descriptor = null,
          gps = "test",
          default = false,
          address = null
        )
        addressService.updateAndSaveAddress(addressRequestDto)
      }
      it("Error handling for insert record "){
        var addressService = AddressServices(
          addressRepository = addressRepositoryInsertError,
          mapper = mapper,
          responseStorageService = responseStorageServiceNoData
        )
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
        var addressRequestDto = DeliveryAddressRequestDto(
          descriptor = null,
          gps = "test",
          default = false,
          address = null
        )
        addressService.updateAndSaveAddress(addressRequestDto)
      }
    }
    it("findAddressesForCurrentUser returns database error"){
      var addressService = AddressServices(
        addressRepository = addressRepository,
        mapper = mapper,
        responseStorageService = responseStorageServiceNoData
      )
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
      addressService.findAddressesForCurrentUser("test").statusCode shouldBe DatabaseError.NoDataFound.status()
    }
    it("findAddressesForCurrentUser returns list of delivery addresses"){
      var addressService = AddressServices(
        addressRepository = addressRepository,
        mapper = mapper,
        responseStorageService = responseStorageService
      )
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
      addressService.findAddressesForCurrentUser("test").statusCode shouldBe  HttpStatus.OK
    }
  }
}