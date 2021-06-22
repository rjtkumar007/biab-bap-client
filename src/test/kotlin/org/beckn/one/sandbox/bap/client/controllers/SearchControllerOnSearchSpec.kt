package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.message.entities.Message
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.hamcrest.CoreMatchers.`is`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class SearchControllerOnSearchSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val contextFactory: ContextFactory,
  val uuidFactory: UuidFactory,
  val messageRepository: GenericRepository<Message>
) : DescribeSpec() {
  init {

    describe("On Search") {
      it("should return error bad request response when message id is null") {
        mockMvc
          .perform(
            get("/v1/on_search")
              .param("messageId", null)
          )
          .andExpect(status().isBadRequest)
      }

      it("should return error response when message id is invalid") {
        val nonExistentMessageId = uuidFactory.create()
        mockMvc
          .perform(
            get("/v1/on_search")
              .param("messageId", nonExistentMessageId)
          )
          .andExpect(status().isNotFound)
          .andExpect(jsonPath("$.error.code", `is`("BAP_008")))
          .andExpect(jsonPath("$.error.message", `is`("No message with the given ID")))
      }
    }
  }
}
