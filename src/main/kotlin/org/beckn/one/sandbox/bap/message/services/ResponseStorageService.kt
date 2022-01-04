package org.beckn.one.sandbox.bap.message.services

import arrow.core.Either
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.DeleteResult
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.BecknResponseDao
import org.beckn.one.sandbox.bap.message.mappers.GenericResponseMapper
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.bson.conversions.Bson
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface ResponseStorageService<Proto : ClientResponse, Entity : BecknResponseDao> {
  fun save(protoResponse: Entity): Either<DatabaseError.OnWrite, Proto>
  fun findManyByUserId(id: String,skip: Int , limit :Int): Either<DatabaseError, List<Proto>>
  fun findById(userId: String): Either<DatabaseError, Proto?>
  fun findOrdersById(bson: Bson, skip: Int  , limit :Int ): Either<DatabaseError, List<Proto>>
  fun updateDocByQuery(query: Bson, requestData: Entity): Either<DatabaseError, Proto>
  fun deleteOneById(id: String): Either<DatabaseError, DeleteResult>
  fun findOrderId(bson: Bson): Either<DatabaseError, Entity>
}

class ResponseStorageServiceImpl<Proto : ClientResponse, Entity : BecknResponseDao> constructor(
  private val responseRepository: BecknResponseRepository<Entity>,
  val mapper: GenericResponseMapper<Proto, Entity>
) : ResponseStorageService<Proto, Entity> {
  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  override fun save(requestDao: Entity): Either<DatabaseError.OnWrite, Proto> =
    Either
      .catch {
        log.info("Saving client response: {}", requestDao)
        responseRepository.insertOne(requestDao)
      }
      .bimap(
        rightOperation = {
          mapper.entityToProtocol(it)
        },
        leftOperation = {
          log.error("Exception while saving search response", it)
          DatabaseError.OnWrite
        }
      )

  private fun toSchema(allResponses: List<Entity>) =
    allResponses.map { response -> mapper.entityToProtocol(response) }

  override fun findManyByUserId(id: String ,skip: Int , limit :Int): Either<DatabaseError, List<Proto>> = Either
    .catch { responseRepository.findManyByUserId(id) }
    .map {
      return if (it.isNotEmpty()) {
        Either.Right(toSchema(it))
      } else {
        Either.Left(DatabaseError.NoDataFound)
      }
    }
    .mapLeft { e ->
      log.error("Exception while fetching search response", e)
      DatabaseError.OnRead
    }

  override fun findById(userId: String): Either<DatabaseError, Proto?> = Either
    .catch { responseRepository.findById(userId) }
    .mapLeft { e ->
      log.error("Exception while fetching search response", e)
      DatabaseError.OnRead
    }.map { data ->
      return if (data != null) {
        Either.Right(mapper.entityToProtocol(data))
      } else {
        Either.Left(DatabaseError.NoDataFound)
      }
    }

  override fun updateDocByQuery(query: Bson, requestData: Entity): Either<DatabaseError, Proto> =
    Either
      .catch {
        responseRepository.updateByIdQuery(query, requestData, UpdateOptions().upsert(true))
      }.mapLeft { e ->
        log.error("Error while updating user by id ", e)
        DatabaseError.OnWrite
      }.map {
        mapper.entityToProtocol(requestData)
      }

  override fun deleteOneById(id: String): Either<DatabaseError, DeleteResult> =
    Either
      .catch {
        responseRepository.deleteOneById(id)
      }.mapLeft { e ->
        log.error("Error while updating user by id ", e)
        DatabaseError.OnWrite
      }.map {
        return if (it != null) {
          Either.Right(it)
        } else {
          Either.Left(DatabaseError.OnWrite)
        }
      }

  override fun findOrdersById(bson: Bson,skip: Int , limit :Int ): Either<DatabaseError, List<Proto>> = Either
    .catch { responseRepository.findOrdersById(bson, skip, limit) }
    .map {
      return if (it.isNotEmpty()) {
        Either.Right(toSchema(it))
      } else {
        Either.Left(DatabaseError.NoDataFound)
      }
    }
    .mapLeft { e ->
      log.error("Exception while fetching search response", e)
      DatabaseError.OnRead
    }

  override fun findOrderId(bson: Bson): Either<DatabaseError, Entity> = Either
    .catch { responseRepository.findOne(bson) }
    .mapLeft { e ->
      log.error("Exception while fetching search response", e)
      DatabaseError.OnRead
    }.map { data ->
      return if (data != null) {
        Either.Right(data)
      } else {
        Either.Left(DatabaseError.NotFound)
      }
    }

}