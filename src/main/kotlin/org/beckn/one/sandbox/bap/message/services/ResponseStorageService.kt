package org.beckn.one.sandbox.bap.message.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.BecknResponseDao
import org.beckn.one.sandbox.bap.message.mappers.GenericResponseMapper
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface ResponseStorageService<Proto : ClientResponse, Entity : BecknResponseDao> {
  fun save(protoResponse: Entity): Either<DatabaseError.OnWrite, Proto>
  fun findByUserId(id: String): Either<DatabaseError.OnRead, List<Proto>>
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

  override fun findByUserId(id: String): Either<DatabaseError.OnRead, List<Proto>> = Either
    .catch { responseRepository.findByUserId(id) }
    .map {
      toSchema(it)
    }
    .mapLeft { e ->
      log.error("Exception while fetching search response", e)
      DatabaseError.OnRead
    }

}