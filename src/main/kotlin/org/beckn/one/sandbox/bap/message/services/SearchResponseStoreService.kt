package org.beckn.one.sandbox.bap.message.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.services.SearchService
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.SearchResponse
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class SearchResponseStoreService(
  @Autowired @Qualifier("search-repo") val searchResponseRepo: BecknResponseRepository<SearchResponse>
) {
  private val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun save(response: SearchResponse) = Either
    .catch { searchResponseRepo.insertOne(response) }
    .mapLeft { e ->
      log.error("Exception while saving search response", e)
      DatabaseError.OnWrite
    }


  fun findByMessageId(id: String) = Either
    .catch { searchResponseRepo.findByMessageId(id) }
    .mapLeft { e ->
      log.error("Exception while fetching search response", e)
      DatabaseError.OnRead
    }
}