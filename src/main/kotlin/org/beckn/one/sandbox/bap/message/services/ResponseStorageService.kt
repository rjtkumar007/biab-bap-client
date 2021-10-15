package org.beckn.one.sandbox.bap.message.services

import arrow.core.Either
import com.mongodb.client.model.UpdateOptions
import org.beckn.one.sandbox.bap.client.shared.dtos.AccountDetailsResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.BecknResponseDao
import org.beckn.one.sandbox.bap.message.mappers.GenericResponseMapper
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface ResponseStorageService<Proto : ClientResponse, Entity : BecknResponseDao> {
  fun save(protoResponse: Entity): Either<DatabaseError.OnWrite, Proto>
  fun findManyByUserId(id: String): Either<DatabaseError, List<Proto>>
  fun findByUserId(id: String): Either<DatabaseError, Proto?>
  fun updateOneById(requestData: Entity): Either<DatabaseError, Proto?>
  fun findGraphData(id: String): Either<DatabaseError, Proto?>
}

class ResponseStorageServiceImpl<Proto : ClientResponse, Entity : BecknResponseDao> constructor(
  private val responseRepository: BecknResponseRepository<Entity>, val mapper: GenericResponseMapper<Proto, Entity>
) : ResponseStorageService<Proto, Entity> {
  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  override fun save(requestDao: Entity): Either<DatabaseError.OnWrite, Proto> =
    Either
      .catch {
        log.info("Saving protocol response: {}", requestDao)
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

  override fun findManyByUserId(id: String): Either<DatabaseError, List<Proto>> = Either
    .catch { responseRepository.findManyByUserId(id) }
    .map {
      return if(it.isNotEmpty()){
        Either.Right(toSchema(it))
      }else{
        Either.Left(DatabaseError.NotFound)
      }
    }
    .mapLeft { e ->
      log.error("Exception while fetching search response", e)
      DatabaseError.OnRead
    }

  override fun findByUserId(id: String): Either<DatabaseError, Proto?> = Either
  .catch { responseRepository.findByUserId(id) }
  .mapLeft { e ->
    log.error("Exception while fetching search response", e)
    DatabaseError.OnRead
  }.map{
      data->
      return if(data != null) {
        Either.Right(mapper.entityToProtocol(data))
      } else{
        Either.Left(DatabaseError.NotFound)
      }
    }

  override fun updateOneById(requestData: Entity): Either<DatabaseError, Proto?> = Either
    .catch {
      return when (val message = responseRepository.updateOneEntityById(requestData,UpdateOptions().upsert(true))) {
      null -> Either.Left(DatabaseError.NotFound)
      else -> Either.Right(mapper.entityToProtocol(requestData))
    }}.mapLeft { e ->
        log.error("Error while updating user by id ", e)
        DatabaseError.OnWrite
      }

  override fun findGraphData(id: String): Either<DatabaseError, Proto?> =
    Either
      .catch {
        responseRepository.findDataFromAllCollections(id)
      }.mapLeft { e ->
        log.error("Error while updating user by id ", e)
        DatabaseError.OnWrite
      }.map {
        val d = it
        null
      }

}