package org.beckn.one.sandbox.bap.client.order.confirm.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderResponse
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.mappers.GenericResponseMapper
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.litote.kmongo.eq
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OnConfirmOrderService @Autowired constructor(
  val repository: ResponseStorageService<OrderResponse, OrderDao>,
  val mapper: GenericResponseMapper<OrderResponse, OrderDao>,
  private val log: Logger = LoggerFactory.getLogger(OnConfirmOrderService::class.java)
) {
   fun updateOrder(orderDao: OrderDao):Either<DatabaseError, ClientResponse>{
     return if(orderDao.transactionId == null){
       log.error("Transaction id is not available")
       Either.Left(DatabaseError.NoDataFound)
     }else{
       log.error("Updating db on confirm callback")
       repository.updateDocByQuery(OrderDao::messageId eq orderDao.messageId, orderDao)
     }
   }

  fun findById(messageId: String?):Either<DatabaseError, OrderDao>{
    return if(messageId.isNullOrEmpty()){
      log.error("Message id is not available")
      Either.Left(DatabaseError.NotFound)
    }else {
      repository.findOrderId(OrderDao::messageId eq messageId)
    }
  }
}
