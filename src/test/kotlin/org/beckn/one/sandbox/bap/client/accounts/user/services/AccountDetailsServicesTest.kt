package org.beckn.one.sandbox.bap.client.accounts.user.services

import arrow.core.Either
import arrow.core.right
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.accounts.address.services.AddressServices
import org.beckn.one.sandbox.bap.client.accounts.billings.services.BillingDetailService
import org.beckn.one.sandbox.bap.client.shared.dtos.*
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.message.entities.AccountDetailsDao
import org.beckn.one.sandbox.bap.message.entities.BecknResponseDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder


internal class AccountDetailsServicesTest : DescribeSpec() {

  private val accountDetailsResponse = accountDetailsResponse(userName = "Khanna",userEmail = "simplyrajat010@gmail.com",userPhone = "9898928272")
  private val responseStorageService = mock<ResponseStorageService<AccountDetailsResponse, AccountDetailsDao>> {
    onGeneric { findById(any()) }.thenReturn(Either.Right(accountDetailsResponse))
  }

  private val responseStorageServiceNoData = mock<ResponseStorageService<AccountDetailsResponse, AccountDetailsDao>> {
    onGeneric { findById(any()) }.thenReturn(Either.Left(DatabaseError.NoDataFound))
  }

  private val responseStorageServiceDataWriteError = mock<ResponseStorageService<AccountDetailsResponse, AccountDetailsDao>> {
    onGeneric { updateDocByQuery(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnWrite))
  }
  private val responseStorageServiceSaveAccount = mock<ResponseStorageService<AccountDetailsResponse, AccountDetailsDao>> {
    onGeneric { updateDocByQuery(any(), any()) }.thenReturn(Either.Right(accountDetailsResponse))
  }

  private val addressServices = Mockito.mock(AddressServices::class.java)
  private val billingDetailService = Mockito.mock(BillingDetailService::class.java)

  private val accountServicesNoData = AccountDetailsServices(
    addressServices = addressServices,
    billingDetailService = billingDetailService,
    responseStorageService = responseStorageServiceNoData
  )

  private val accountServices = AccountDetailsServices(
    addressServices = addressServices,
    billingDetailService = billingDetailService,
    responseStorageService = responseStorageService
  )

  private val accountServicesDataWriteError = AccountDetailsServices(
    addressServices = addressServices,
    billingDetailService = billingDetailService,
    responseStorageService = responseStorageServiceDataWriteError
  )

  private val accountServicesSaveAccount = AccountDetailsServices(
    addressServices = addressServices,
    billingDetailService = billingDetailService,
    responseStorageService = responseStorageServiceSaveAccount
  )


  init {
    describe("AccountDetailsServices test case"){
      it("Should call findAccountDetailForCurrentUser with out user id and fail properly") {
        var accountDetailResponse = accountServicesNoData.findAccountDetailForCurrentUser("");
        accountDetailResponse.statusCode shouldBe DatabaseError.NoDataFound.status()
      }
      it("Should call findAccountDetailForCurrentUser with user id and will return status OK") {
        Mockito.`when`(addressServices.findAddressesForCurrentUser(any())).thenReturn(listOfDeliveryAddressResponse())
        Mockito.`when`(billingDetailService.findBillingsForCurrentUser(any())).thenReturn(listOfBillingResponse())
        var accountDetailResponse = accountServices.findAccountDetailForCurrentUser("");
        accountDetailResponse.statusCode shouldNotBe DatabaseError.NoDataFound.status()
      }
      it("Should fail with UNAUTHORIZED error when called updateAccountDetails without a current user"){
        var updateAccountDetailsRespose = accountServices.updateAccountDetails(null,getAccountRequestDto("","",""))
        updateAccountDetailsRespose.statusCode shouldBe BppError.AuthenticationError.status()
      }
      var user = User(
          uid = "1234533434343",
          name = "John",
          email = "john@gmail.com",
          isEmailVerified = true
        )
      it("Should fail with BAD_REQUEST error when called updateAccountDetails with invalid email address"){
        var updateAccountDetailsRespose = accountServices.updateAccountDetails(user,getAccountRequestDto("","","test"))
        updateAccountDetailsRespose.statusCode shouldBe BppError.BadRequestError.status()
      }
      it("Generates DatabaseError properly for a failed persistence into DB"){
        var updateAccountDetailsRespose = accountServicesDataWriteError.updateAccountDetails(user,getAccountRequestDto("John","","john@gmail.com"))
        updateAccountDetailsRespose.statusCode shouldBe DatabaseError.OnWrite.status()
      }
      it("Save account detail and respond with AccountDetailsResponse"){
        var updateAccountDetailsRespose = accountServicesSaveAccount.updateAccountDetails(user,getAccountRequestDto("John","","john@gmail.com"))
        updateAccountDetailsRespose.statusCode shouldNotBe DatabaseError.OnWrite.status()
      }
      it("Save account detail and respond with AccountDetailsResponse and failes on Firebase user update for different email address"){
        var updateAccountDetailsRespose = accountServicesSaveAccount.updateAccountDetails(user,getAccountRequestDto("John","","john@gmail2.com"))
        updateAccountDetailsRespose.statusCode shouldBe BppError.BadRequestError.status()
      }
    }
  }

  private  fun accountDetailsResponse(userName: String = "", userPhone: String = "", userEmail: String = ""): AccountDetailsResponse {
    val accountDetailsResponse = AccountDetailsResponse(
      context = null,
      name = userName,
      error = null,
      userId = null,
      email = userEmail,
      phone = userPhone
    )
    return accountDetailsResponse
  }
  private fun listOfDeliveryAddressResponse() : ResponseEntity<List<DeliveryAddressResponse>>{
    var listOfAddress = ArrayList<DeliveryAddressResponse>()
    return ResponseEntity.ok(listOfAddress)
  }
  private fun listOfBillingResponse() : ResponseEntity<List<BillingDetailsResponse>>{
    var listOfAddress = ArrayList<BillingDetailsResponse>()
    return ResponseEntity.ok(listOfAddress)
  }
  private fun getAccountRequestDto(userName: String = "", userPhone: String = "", userEmail: String = ""): AccountRequestDto {

    return  AccountRequestDto(
      userName = userName,
      userPhone = userPhone,
      userEmail = userEmail
    )
  }
}