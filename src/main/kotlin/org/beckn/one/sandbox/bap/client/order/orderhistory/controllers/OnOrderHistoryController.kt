package org.beckn.one.sandbox.bap.client.order.orderhistory.controllers

import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.order.orderhistory.services.OrderServices
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.errors.HttpError
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
class OnOrderHistoryController @Autowired constructor(
  val orderServices: OrderServices
){

  @RequestMapping("/client/v1/orders")
  @ResponseBody
  fun onOrdersList (
    @RequestParam orderId: String?,
    @RequestParam parentOrderId: String?,
    @RequestParam skip: Int?,
    @RequestParam limit: Int?
  ) :ResponseEntity<List<OrderResponse>>{
    val user = SecurityUtil.getSecuredUserDetail()
    return if(user != null){
      orderServices.findAllOrders(user,orderId?: "", parentOrderId?: "",skip?:0,limit?:10).fold(
        {
          mapToErrorResponse(it)
        },
        {
          ResponseEntity.ok(it)
        }
      )
    }else{
      mapToErrorResponse(BppError.AuthenticationError)
    }
  }

  private fun mapToErrorResponse(it: HttpError) = ResponseEntity
    .status(it.status())
    .body(
     listOf(
       OrderResponse(
         context = null,
         error = it.error(),
         userId = null
       )
     )
    )
}