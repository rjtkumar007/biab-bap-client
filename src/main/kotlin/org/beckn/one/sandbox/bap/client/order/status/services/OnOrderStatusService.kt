package org.beckn.one.sandbox.bap.client.order.status.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderResponse
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.litote.kmongo.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OnOrderStatusService @Autowired constructor(
  val repository: ResponseStorageService<OrderResponse, OrderDao>
  ) {
   fun updateOrder(orderDao: OrderDao):Either<DatabaseError, ClientResponse>{
     return repository.updateDocByQuery(OrderDao::id eq orderDao.id!!, orderDao)
   }
}
