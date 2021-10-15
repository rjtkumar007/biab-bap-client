package org.beckn.one.sandbox.bap.client.accounts.billings.controllers

import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.accounts.billings.services.BillingDetailService
import org.beckn.one.sandbox.bap.client.shared.dtos.BillingDetailsResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class OnBillingDetailsController @Autowired constructor(
  private val billingService: BillingDetailService
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @RequestMapping("/client/v1/billing_details")
  @ResponseBody
  fun onBillingDetails(): ResponseEntity<out List<BillingDetailsResponse>> {
    val user = SecurityUtil.getSecuredUserDetail()
    return billingService.findBillingsForCurrentUser(user?.uid!!)
  }

}