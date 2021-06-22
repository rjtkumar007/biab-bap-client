package org.beckn.one.sandbox.bap.message.repositories

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.ints.shouldBeExactly
import org.beckn.one.sandbox.bap.configurations.DatabaseConfiguration
import org.beckn.one.sandbox.bap.configurations.TestDatabaseConfiguration
import org.beckn.one.sandbox.bap.message.entities.Catalog
import org.beckn.one.sandbox.bap.message.entities.Context
import org.beckn.one.sandbox.bap.message.entities.SearchResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@SpringBootTest(classes = [TestDatabaseConfiguration::class, DatabaseConfiguration::class])
class BecknResponseRepositorySpec : DescribeSpec() {
  @Autowired
  @Qualifier("search-repo")
  private lateinit var repo: BecknResponseRepository<SearchResponse>

  init {
    describe("Generic Repository") {

      context("for SearchResponse") {
        val searchResponse = SearchResponse(
          context = context,
          message = Catalog()
        )

        it("should fetch responses by message id") {
          repo.clear()
          repo.insertMany(
            listOf(
              searchResponse,
              searchResponse,
              searchResponse.copy(context = context.copy(messageId = "123"))
            )
          )
          repo.findByMessageId(context.messageId).size shouldBeExactly 2
        }
      }
    }
  }

  private val fixedClock = Clock.fixed(
    Instant.parse("2018-11-30T18:35:24.00Z"),
    ZoneId.of("Asia/Calcutta")
  )

  private val context = Context(
    domain = "LocalRetail",
    country = "IN",
    action = Context.Action.SEARCH,
    city = "Pune",
    coreVersion = "0.9.1-draft03",
    bapId = "http://host.bap.com",
    bapUri = "http://host.bap.com",
    transactionId = "222",
    messageId = "222",
    timestamp = LocalDateTime.now(fixedClock)
  )
}
