package org.beckn.one.sandbox.bap.client.accounts.user.services

import arrow.core.Either
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.UserDao
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.litote.kmongo.eq
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserAccountServices @Autowired constructor(
  private val userRepository: GenericRepository<UserDao>
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun save(message: UserDao): Either<HttpError, UserDao> = Either
    .catch { userRepository.insertOne(message) }
    .mapLeft { e ->
      log.error("Error when saving message to DB", e)
      DatabaseError.OnWrite
    }

  fun findById(id: String): Either<HttpError, UserDao?> {
    return Either
      .catch {
        return when (val message = userRepository.findOne(UserDao::userId eq id)){
          null -> Either.Right(null)
          else -> Either.Right(message)
        }
      }
      .mapLeft { e ->
        log.error("Error when fetching user by id $id", e)
        DatabaseError.OnRead
      }
  }

  fun updateUserById(userDao: UserDao): Either<HttpError, UpdateResult?> {
    return Either
      .catch {
        return when (val message = userRepository.update(userDao.userId!!,
          userDao,
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
}
