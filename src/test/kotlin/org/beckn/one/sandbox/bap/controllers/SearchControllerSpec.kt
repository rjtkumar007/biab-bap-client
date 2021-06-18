package org.beckn.one.sandbox.bap.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.configurations.TestDatabaseConfiguration
import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.dtos.ResponseStatus.ACK
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.MockNetwork
import org.beckn.one.sandbox.bap.factories.ResponseFactory
import org.beckn.one.sandbox.bap.repositories.MessageRepository
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class SearchControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val contextFactory: ContextFactory,
  val messageRepository: MessageRepository
) : DescribeSpec() {
  init {

    describe("Search") {
      MockNetwork.startAllSubscribers()
      beforeEach {
        MockNetwork.resetAllSubscribers()
      }

      it("should return error response when registry lookup fails") {
        MockNetwork.registry
          .stubFor(post("/lookup").willReturn(serverError()))

        mockMvc
          .perform(
            get("/v1/search")
              .param("searchString", "Fictional mystery books")
          )
          .andExpect(status().is5xxServerError)
          .andExpect(jsonPath("$.message.ack.status", `is`("NACK")))
          .andExpect(jsonPath("$.error.code", `is`("BAP_001")))
          .andExpect(jsonPath("$.error.message", `is`("Registry lookup returned error")))
      }

      it("should invoke Beckn /search API on first gateway and persist message") {
        val gatewaysJson = objectMapper.writeValueAsString(MockNetwork.getAllGateways())
        MockNetwork.registry
          .stubFor(post("/lookup").willReturn(okJson(gatewaysJson)))
        MockNetwork.retailBengaluruBg
          .stubFor(
            post("/search").willReturn(
              okJson(
                objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory))
              )
            )
          )

        val result: MvcResult = mockMvc
          .perform(
            get("/v1/search")
              .param("searchString", "Fictional mystery books")
          )

          .andExpect(status().is2xxSuccessful)
          .andExpect(jsonPath("$.message.ack.status", `is`(ACK.status)))
          .andExpect(jsonPath("$.context.message_id", `is`(notNullValue())))
          .andReturn()
        MockNetwork.retailBengaluruBg.verify(postRequestedFor(urlEqualTo("/search")))
        val searchResponse = objectMapper.readValue(result.response.contentAsString, Response::class.java)
        val savedMessage = messageRepository.findById(searchResponse.context.messageId)
        savedMessage shouldNotBe null
        savedMessage?.id shouldBe searchResponse.context.messageId
      }
    }
  }
}
