package org.beckn.one.sandbox.bap.message.services

import com.mongodb.MongoException
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.ints.shouldBeExactly
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.SearchResponse
import org.beckn.one.sandbox.bap.message.factories.CatalogFactory
import org.beckn.one.sandbox.bap.message.mappers.SearchResponseMapper
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponseMessage
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@SpringBootTest
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
internal class ResponseStorageServiceSpec @Autowired constructor(
  val searchResponseMapper: SearchResponseMapper,
  val searchResponseStorageService: ResponseStorageService<ProtocolSearchResponse>,
  val searchResponseRepo: BecknResponseRepository<SearchResponse>
) : DescribeSpec() {


  private val fixedClock = Clock.fixed(
    Instant.parse("2018-11-30T18:35:24.00Z"),
    ZoneId.of("Asia/Calcutta")
  )

  private val context = org.beckn.one.sandbox.bap.schemas.ProtocolContext(
    domain = "LocalRetail",
    country = "IN",
    action = org.beckn.one.sandbox.bap.schemas.ProtocolContext.Action.SEARCH,
    city = "Pune",
    coreVersion = "0.9.1-draft03",
    bapId = "http://host.bap.com",
    bapUri = "http://host.bap.com",
    transactionId = "222",
    messageId = "222",
    timestamp = LocalDateTime.now(fixedClock)
  )

  val schemaSearchResponse = org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponse(
    context = context,
    message = ProtocolSearchResponseMessage(CatalogFactory.create(2))
  )

  init {
    describe("SearchResponseStore") {
      val searchResponse = searchResponseMapper.protocolToEntity(schemaSearchResponse)

      context("when save is called with search response") {
        searchResponseRepo.clear()
        val response = searchResponseStorageService.save(schemaSearchResponse)

        it("should save response to store") {
          searchResponseRepo.all().size shouldBeExactly 1
        }

        it("should respond with either.right to indicate success") {
          response.shouldBeRight()
        }
      }

      context("when findByMessageID is called with id") {
        searchResponseRepo.clear()
        searchResponseRepo.insertOne(searchResponse)
        val response = searchResponseStorageService.findByMessageId(context.messageId)

        it("should respond with either.right containing the search results") {
          response.shouldBeRight(listOf(schemaSearchResponse))
        }
      }

      context("when error is encountered while saving") {
        val mockRepo = mock<BecknResponseRepository<SearchResponse>> {
          onGeneric { insertOne(searchResponse) }.thenThrow(MongoException("Write error"))
        }
        val failureSearchResponseService = ResponseStorageServiceImpl(mockRepo, searchResponseMapper)
        val response = failureSearchResponseService.save(schemaSearchResponse)

        it("should return a left with write error") {
          response.shouldBeLeft(DatabaseError.OnWrite)
        }
      }

      context("when error is encountered while fetching message by id") {
        val mockRepo = mock<BecknResponseRepository<SearchResponse>> {
          onGeneric { findByMessageId(context.messageId) }.thenThrow(MongoException("Write error"))
        }
        val failureSearchResponseService = ResponseStorageServiceImpl(mockRepo, searchResponseMapper)
        val response = failureSearchResponseService.findByMessageId(context.messageId)

        it("should return a left with write error") {
          response.shouldBeLeft(DatabaseError.OnRead)
        }
      }
    }
  }
}