package org.beckn.one.sandbox.bap.client.order.orders.services

import arrow.core.Either
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates
import com.mongodb.client.result.UpdateResult
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.entities.UserDao
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.bson.conversions.Bson
import org.litote.kmongo.eq
import org.litote.kmongo.set
import org.litote.kmongo.setValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrderServices @Autowired constructor(
  private val orderRepository: GenericRepository<OrderDao>
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun save(orderDao: OrderDao): Either<HttpError, OrderDao> = Either
    .catch { orderRepository.insertOne(orderDao) }
    .mapLeft { e ->
      log.error("Error when saving message to DB", e)
      DatabaseError.OnWrite
    }

  fun findById(id: String): Either<HttpError, OrderDao?> {
    return Either
      .catch {
        return when (val message = orderRepository.findOne(OrderDao::userId eq id)){
          null -> Either.Right(null)
          else -> Either.Right(message)
        }
      }
      .mapLeft { e ->
        log.error("Error when fetching user by id $id", e)
        DatabaseError.OnRead
      }
  }

  fun updateUserById(orderDao: OrderDao): Either<HttpError, UpdateResult?> {
    return Either
      .catch {
        return when (val message = orderRepository.update(orderDao.userId!!,
          orderDao,
          UpdateOptions().upsert(true))) {
          null -> Either.Right(null)
          else -> Either.Right(message)
        }
      }
      .mapLeft { e ->
        log.error("Error while updating user by id ", e)
        DatabaseError.OnRead
      }
  }


  /*fun findAllUser(): Either<HttpError, UserDao?> {
    return Either
      .catch {
        return when (val message = userRepository.findOne(UserDao::userId eq id)) {
          null -> Either.Right(null)
          else -> Either.Right(message)
        }
      }
      .mapLeft { e ->
        log.error("Error when fetching user by id $id", e)
        DatabaseError.OnRead
      }
  }*/
}
