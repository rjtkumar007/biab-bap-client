package org.beckn.one.sandbox.bap.message.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.services.SearchService
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.SearchResponse
import org.beckn.one.sandbox.bap.message.mappers.SearchResponseMapper
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class SearchResponseStoreService @Autowired constructor(
  @Qualifier("search-repo") val searchResponseRepo: BecknResponseRepository<SearchResponse>,
  val searchResponseMapper: SearchResponseMapper
) {
  private val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun save(response: org.beckn.one.sandbox.bap.schemas.SearchResponse): Either<DatabaseError.OnWrite, org.beckn.one.sandbox.bap.schemas.SearchResponse> = Either
    .catch { searchResponseRepo.insertOne(searchResponseMapper.fromSchema(response)) }
    .bimap(
      rightOperation = { response },
      leftOperation = {
        log.error("Exception while saving search response", it)
        DatabaseError.OnWrite
      }
    )


  fun findByMessageId(id: String) = Either
    .catch { searchResponseRepo.findByMessageId(id) }
    .mapLeft { e ->
      log.error("Exception while fetching search response", e)
      DatabaseError.OnRead
    }
}