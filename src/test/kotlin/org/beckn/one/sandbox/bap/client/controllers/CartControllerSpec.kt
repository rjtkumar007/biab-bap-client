package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.daos.CartDao
import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.dtos.CartItemDto
import org.beckn.one.sandbox.bap.client.dtos.CartItemProviderDto
import org.beckn.one.sandbox.bap.client.dtos.CreateCartResponseDto
import org.beckn.one.sandbox.bap.client.mappers.CartMapper
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import org.litote.kmongo.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class CartControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val cartRepository: GenericRepository<CartDao>,
  val cartMapper: CartMapper
) : DescribeSpec() {
  init {
    describe("Cart") {

      it("should create cart") {
        val cart = getCart(null)

        val createCartResponseString = mockMvc
          .perform(
            post("/client/v1/cart")
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .content(objectMapper.writeValueAsString(cart))
          )
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val createCartResponse = objectMapper.readValue(createCartResponseString, CreateCartResponseDto::class.java)
        val expectedCartDto = cart.copy(id = createCartResponse.message.cart.id)
        createCartResponse.context shouldNotBe null
        createCartResponse.message.cart.id shouldNotBe null
        createCartResponse.message.cart shouldBe expectedCartDto
        val cartFromDb = cartRepository.findOne(CartDto::id eq createCartResponse.message.cart.id)
        cartFromDb shouldNotBe null
        cartFromDb?.let { cartMapper.daoToDto(it) } shouldBe expectedCartDto
      }
    }
  }

  private fun getCart(cartId: String?) = CartDto(
    id = cartId,
    items = listOf(
      CartItemDto(
        bppId = "paisool",
        provider = CartItemProviderDto(
          id = "venugopala stores",
          providerLocations = listOf("13.001581,77.5703686")
        ),
        itemId = "cothas-coffee-1",
        quantity = 2,
        measure = ProtocolScalar(
          value = BigDecimal.valueOf(500),
          unit = "gm"
        )
      ),
      CartItemDto(
        bppId = "paisool",
        provider = CartItemProviderDto(
          id = "maruthi-stores",
          providerLocations = listOf("12.9995218,77.5704439")
        ),
        itemId = "malgudi-coffee-500-gms",
        quantity = 1,
        measure = ProtocolScalar(
          value = BigDecimal.valueOf(1),
          unit = "kg"
        )
      )
    )
  )
}
