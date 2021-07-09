package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.dtos.CartResponseDto
import org.beckn.one.sandbox.bap.client.dtos.CartResponseMessageDto
import org.beckn.one.sandbox.bap.client.factories.CartFactory
import org.beckn.one.sandbox.bap.schemas.ProtocolAckResponse
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class MockCartControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
) : DescribeSpec() {
  init {
    describe("Cart") {

      it("should return ACK response for create or update cart API") {
        val cart = getCart("cart 1")

        val createCartResponseString = mockMvc
          .perform(
            put("/client/v0/cart")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(cart))
          )
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val createCartResponse = objectMapper.readValue(createCartResponseString, ProtocolAckResponse::class.java)
        createCartResponse.context shouldNotBe null
        createCartResponse.context?.messageId shouldNotBe null
        createCartResponse.message shouldBe ResponseMessage.ack()
      }

      it("should return on cart response") {
        val messageId = "message id 1"

        val onCartResponseString = mockMvc
          .perform(
            get("/client/v0/on_cart")
              .param("message_id", messageId)
          )
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val onCartResponse = objectMapper.readValue(onCartResponseString, CartResponseDto::class.java)
        onCartResponse.context shouldNotBe null
        onCartResponse.context.messageId shouldBe messageId
        onCartResponse.message shouldBe CartResponseMessageDto(getCart("cart 1"))
      }

      it("should return cart by id") {
        val cartId = "cart 1"

        val getCartResponseString = mockMvc
          .perform(
            get("/client/v0/cart/$cartId")
          )
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val getCartResponse = objectMapper.readValue(getCartResponseString, CartResponseDto::class.java)
        getCartResponse.message shouldBe CartResponseMessageDto(cart = getCart(cartId))
      }
    }
  }

  private fun getCart(cartId: String) =
    CartFactory.create(id = cartId, transactionId = "ac99a617-5065-4ae8-9695-d9de3d80f030")
}
