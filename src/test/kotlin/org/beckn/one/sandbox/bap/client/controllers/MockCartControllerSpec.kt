package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.dtos.*
import org.beckn.one.sandbox.bap.schemas.*
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
import java.math.BigDecimal

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
        createCartResponse.context.messageId shouldNotBe null
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

  private fun getCart(cartId: String) = CartDto(
    id = cartId,
    items = listOf(
      CartItemDto(
        descriptor = ProtocolDescriptor(
          name = "Cothas Coffee 1 kg",
          images = listOf("https://i.ibb.co/rZqPDd2/Coffee-2-Cothas.jpg"),
        ),
        price = ProtocolPrice(
          currency = "INR",
          value = "500"
        ),
        id = "cothas-coffee-1",
        bppId = "paisool",
        provider = CartItemProviderDto(
          id = "venugopala stores",
          providerLocations = listOf("13.001581,77.5703686")
        ),
        quantity = 1,
        measure = ProtocolScalar(
          value = BigDecimal.valueOf(1),
          unit = "kg"
        ),
      ),
      CartItemDto(
        descriptor = ProtocolDescriptor(
          name = "Malgudi Coffee 500 gm",
          images = listOf("https://i.ibb.co/wgXx7K6/Coffee-1-Malgudi.jpg"),
        ),
        price = ProtocolPrice(
          currency = "INR",
          value = "240"
        ),
        id = "malgudi-coffee-500-gm",
        bppId = "paisool",
        provider = CartItemProviderDto(
          id = "venugopala stores",
          providerLocations = listOf("13.001581,77.5703686")
        ),
        quantity = 1,
        measure = ProtocolScalar(
          value = BigDecimal.valueOf(500),
          unit = "gm"
        ),
      ),
    )
  )
}
