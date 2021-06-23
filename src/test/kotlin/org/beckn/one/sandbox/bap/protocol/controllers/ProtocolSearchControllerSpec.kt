package org.beckn.one.sandbox.bap.protocol.controllers

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.SearchResponse
import org.beckn.one.sandbox.bap.message.factories.CatalogFactory
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.beckn.one.sandbox.bap.message.services.SearchResponseStoreService
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
internal class ProtocolSearchControllerSpec : DescribeSpec() {

  @Autowired
  private lateinit var mockMvc: MockMvc

  @Autowired
  private lateinit var mapper: ObjectMapper

  @Autowired
  @Qualifier("search-repo") private lateinit var searchResponseRepo: BecknResponseRepository<SearchResponse>

  private val postOnSearchUrl = "/core/0.9.1-draft03/on_search"

  private val fixedClock = Clock.fixed(
    Instant.parse("2018-11-30T18:35:24.00Z"),
    ZoneId.of("Asia/Calcutta")
  )

  private val context = org.beckn.one.sandbox.bap.schemas.Context(
    domain = "LocalRetail",
    country = "IN",
    action = org.beckn.one.sandbox.bap.schemas.Context.Action.SEARCH,
    city = "Pune",
    coreVersion = "0.9.1-draft03",
    bapId = "http://host.bap.com",
    bapUri = "http://host.bap.com",
    transactionId = "222",
    messageId = "222",
    timestamp = LocalDateTime.now(fixedClock)
  )

  val schemaSearchResponse = org.beckn.one.sandbox.bap.schemas.SearchResponse(
    context = context,
    message = CatalogFactory().create(2)
  )
  init {

    describe("Protocol Search API") {

      context("when posted to with a valid response") {
        searchResponseRepo.clear()
        val postSearchResponse = mockMvc
          .perform(
            post(postOnSearchUrl)
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .content(mapper.writeValueAsBytes(schemaSearchResponse))
          )

        it("should respond with status as 200") {
          postSearchResponse.andExpect(status().isOk)
        }

        it("should save search response in db") {
          searchResponseRepo.findByMessageId(context.messageId).size shouldBeExactly 1
        }
      }

      context("when error occurs when processing request") {
        val mockService = mock<SearchResponseStoreService>{
          onGeneric { save(schemaSearchResponse) }.thenReturn(Either.Left(DatabaseError.OnWrite))
        }
        val controller = ProtocolSearchController(mockService)

        it("should respond with internal server error"){
          val response = controller.onSearch(schemaSearchResponse)
          response.statusCode shouldBe DatabaseError.OnWrite.status()
        }
      }
    }
  }

}