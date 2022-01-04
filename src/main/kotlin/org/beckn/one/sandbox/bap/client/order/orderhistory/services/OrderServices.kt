package org.beckn.one.sandbox.bap.client.order.orderhistory.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderResponse
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.litote.kmongo.eq
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrderServices @Autowired constructor(
  val ordersResponseRepository: ResponseStorageService<OrderResponse,OrderDao>
) {

  val log: Logger = LoggerFactory.getLogger(this::class.java)


  fun findAllOrders(user: User, orderId: String, parentOrderId : String , skip: Int = 0, limit: Int  =10):Either<HttpError,List<OrderResponse>>{
    return if (!orderId.isNullOrEmpty()){
      ordersResponseRepository.findOrdersById(OrderDao::id eq orderId,0,1)
    } else if (!parentOrderId.isNullOrEmpty()) {
      ordersResponseRepository.findOrdersById(OrderDao::parentOrderId eq parentOrderId,0,limit)
    } else{
      ordersResponseRepository.findManyByUserId(user.uid!!,skip,limit)
      }
  }
}
