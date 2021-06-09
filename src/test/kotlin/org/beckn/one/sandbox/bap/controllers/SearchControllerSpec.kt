package org.beckn.one.sandbox.bap.controllers

import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.SandboxBapTestConfig
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@Import(value = [SandboxBapTestConfig::class])
@ActiveProfiles(value = ["test"])
class SearchControllerSpec @Autowired constructor(
  @Autowired val mockMvc: MockMvc
) : DescribeSpec() {
  init {
    describe("Search") {
      it("should acknowledge search request") {
        mockMvc
          .perform(
            get("/search")
              .param("searchString", "Fictional mystery books")
              .param("searchType", "category")
              .param("location", "12.9338635,77.5586436")
              .param("skip", "0")
              .param("limit", "20")
          )
          .andExpect(status().isOk)
          .andExpect(jsonPath("$.status", `is`("ACK")))
          .andExpect(jsonPath("$.message_id", CoreMatchers.isA(String::class.java)))
      }
    }
  }
}
