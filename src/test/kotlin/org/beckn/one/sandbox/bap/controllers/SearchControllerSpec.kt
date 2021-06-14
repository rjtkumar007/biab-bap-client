package org.beckn.one.sandbox.bap.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.serverError
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.SandboxBapTestConfig
import org.beckn.one.sandbox.bap.factories.NetworkMock
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Import(value = [SandboxBapTestConfig::class])
@ActiveProfiles(value = ["test"])
class SearchControllerSpec @Autowired constructor(
  @Autowired val mockMvc: MockMvc,
  @Autowired val objectMapper: ObjectMapper
) : DescribeSpec() {

  init {
    describe("Search") {
      it("should return error response when beckn gateway lookup fails") {
        NetworkMock.createBecknNetwork()
        NetworkMock.registry
          .stubFor(post("/lookup").willReturn(serverError()))

        mockMvc
          .perform(
            get("/search")
              .param("searchString", "Fictional mystery books")
          )
          .andExpect(status().is5xxServerError)
          .andExpect(jsonPath("$.status", `is`("NACK")))
          .andExpect(jsonPath("$.message_id", `is`(nullValue())))
          .andExpect(jsonPath("$.error.code", `is`("BAP_001")))
          .andExpect(jsonPath("$.error.message", `is`("Registry lookup failed")))
      }
    }
  }
}
