package org.beckn.one.sandbox.bap.client.accounts.user.services

import org.beckn.one.sandbox.bap.client.accounts.address.services.AddressServices
import org.beckn.one.sandbox.bap.client.accounts.billings.services.BillingDetailService
import org.beckn.one.sandbox.bap.client.shared.dtos.AccountDetailsResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.BillingDetailsResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressResponse
import org.beckn.one.sandbox.bap.message.entities.AccountDetailsDao
import org.beckn.one.sandbox.bap.message.entities.AddDeliveryAddressDao
import org.beckn.one.sandbox.bap.message.entities.BillingDetailsDao
import org.beckn.one.sandbox.bap.message.entities.DeliveryAddressDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AccountDetailsServices @Autowired constructor(
  private val  addressServices: AddressServices,
  private val billingDetailService: BillingDetailService,
  private val responseStorageService: ResponseStorageService<AccountDetailsResponse, AccountDetailsDao>
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)


  fun findAccountDetailForCurrentUser(
    userId: String
  ): ResponseEntity<AccountDetailsResponse> = responseStorageService
    .findById(userId)
    .fold(
      {
        log.error("Error when finding search response by message id. Error: {}", it)
        ResponseEntity
          .status(it.status().value())
          .body(AccountDetailsResponse(userId = null,error = it.error(),context = null))
      },
      {
        return bindBillingAndAccount(it,userId)

      }
    )

  private fun bindBillingAndAccount(accountResponse: AccountDetailsResponse?, userId: String): ResponseEntity<AccountDetailsResponse> {

    val addressResponse = addressServices.findAddressesForCurrentUser(userId)
    val billingResponse = billingDetailService.findBillingsForCurrentUser(userId)
    if(addressResponse.statusCodeValue == 200){
      accountResponse?.deliveryAddresses=addressResponse.body as List<DeliveryAddressResponse>
    }
    if(billingResponse.statusCodeValue == 200){
      accountResponse?.billingInfo = billingResponse.body as List<BillingDetailsResponse>
    }
    return ResponseEntity.ok(accountResponse)
  }

}
