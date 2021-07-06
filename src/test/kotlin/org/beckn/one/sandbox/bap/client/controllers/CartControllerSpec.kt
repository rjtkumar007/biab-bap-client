package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.daos.CartDao
import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.dtos.CartResponseDto
import org.beckn.one.sandbox.bap.client.factories.CartFactory
import org.beckn.one.sandbox.bap.client.mappers.CartMapper
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.litote.kmongo.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
        val cart = CartFactory.create(null)

        val createCartResponseString = invokeCartCreateOrUpdateApi(cart)
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val createCartResponse = objectMapper.readValue(createCartResponseString, CartResponseDto::class.java)
        assertCreateCartResponse(createCartResponse, cart)
        assertCartIsPersistedInDb(createCartResponse, cart)
      }

      it("should delete cart") {
        val cartId = "abc-123-ne"
        val cartToBeInsertedDao = cartMapper.dtoToDao(CartFactory.create(cartId).copy(id = cartId))
        cartRepository.findOne(cartToBeInsertedDao::id eq cartToBeInsertedDao.id) shouldBe null
        val insertedCartDao = cartRepository.insertOne(cartToBeInsertedDao)
        val cartFromDb = cartRepository.findOne(insertedCartDao::id eq cartToBeInsertedDao.id)
        cartFromDb shouldNotBe null
        cartFromDb?.let { cartMapper.daoToDto(it) } shouldBe CartFactory.create(cartId)
        val createCartResponseString = mockMvc
          .perform(
            MockMvcRequestBuilders.delete("/client/v1/cart/$cartId")
          )
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString
        cartRepository.findOne(insertedCartDao::id eq cartToBeInsertedDao.id) shouldBe null
        //todo: figure out if response body needs to be validated(don't see any use of doing it but check once)
        //todo: add edge case tests at service level to check for DB failure errors and any other exceptions.
      }
    }
  }

  private fun assertCreateCartResponse(createCartResponse: CartResponseDto, cartToBeCreated: CartDto) {
    createCartResponse.context shouldNotBe null
    createCartResponse.message shouldNotBe null
    createCartResponse.message?.cart?.id shouldNotBe null
    val expectedCartDto = cartToBeCreated.copy(id = createCartResponse.message?.cart?.id)
    createCartResponse.message?.cart shouldBe expectedCartDto
  }

  private fun assertCartIsPersistedInDb(createCartResponse: CartResponseDto, cartDto: CartDto) {
    val cartFromDb = cartRepository.findOne(CartDto::id eq createCartResponse.message?.cart?.id)
    cartFromDb shouldNotBe null
    val expectedCartDto = cartDto.copy(id = createCartResponse.message?.cart?.id)
    cartFromDb?.let { cartMapper.daoToDto(it) } shouldBe expectedCartDto
  }

  private fun invokeCartCreateOrUpdateApi(cart: CartDto) = mockMvc
    .perform(
      put("/client/v1/cart")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(cart))
    )

}
