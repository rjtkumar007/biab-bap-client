package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.daos.CartDao
import org.beckn.one.sandbox.bap.client.dtos.CartDtoV0
import org.beckn.one.sandbox.bap.client.dtos.CartItemDtoV0
import org.beckn.one.sandbox.bap.client.dtos.CartItemProviderDtoV0
import org.beckn.one.sandbox.bap.client.dtos.CartResponseDtoV0
import org.beckn.one.sandbox.bap.client.factories.CartFactory
import org.beckn.one.sandbox.bap.client.mappers.CartMapper
import org.beckn.one.sandbox.bap.client.repositories.CartRepository
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
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
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class CartControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val uuidFactory: UuidFactory,
  val cartRepository: CartRepository,
  val genericRepository: GenericRepository<CartDao>,
  val cartMapper: CartMapper
) : DescribeSpec() {
  init {
    describe("Cart") {

      it("should create cart if it does not exist") {
        val cart = CartFactory.create(null)

        val createCartResponseString = invokeCartCreateOrUpdateApi(cart)
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val createCartResponse = objectMapper.readValue(createCartResponseString, CartResponseDtoV0::class.java)
        assertCreateCartResponse(createCartResponse, cart)
        assertCartIsPersistedInDb(createCartResponse, cart)
      }

      it("should update cart if it exists") {
        val existingCartDto = CartFactory.create(uuidFactory.create())
        val existingCartDao = cartMapper.dtoToDao(existingCartDto)
        cartRepository.saveCart(existingCartDao)

        val updatedCartDto = existingCartDto.copy(
          items = listOf(
            CartItemDtoV0(
              bppId = "paisool",
              provider = CartItemProviderDtoV0(
                id = "venugopala stores",
                providerLocations = listOf("13.001581,77.5703686")
              ),
              itemId = "cothas-coffee-1",
              quantity = 2,
              measure = ProtocolScalar(
                value = BigDecimal.valueOf(500),
                unit = "gm"
              )
            )
          )
        )
        val updateCartResponseString = invokeCartCreateOrUpdateApi(updatedCartDto)
          .andExpect(status().`is`(200))
          .andReturn()
          .response.contentAsString

        val updateCartResponse = objectMapper.readValue(updateCartResponseString, CartResponseDtoV0::class.java)
        assertCreateCartResponse(updateCartResponse, updatedCartDto)
        assertCartIsPersistedInDb(updateCartResponse, updatedCartDto)
      }

      it("should delete cart") {
        val cartId = "abc-123-ne"
        val cartToBeInsertedDao = cartMapper.dtoToDao(CartFactory.create(cartId).copy(id = cartId))
        genericRepository.findOne(cartToBeInsertedDao::id eq cartToBeInsertedDao.id) shouldBe null
        val insertedCartDao = genericRepository.insertOne(cartToBeInsertedDao)
        val cartFromDb = genericRepository.findOne(insertedCartDao::id eq cartToBeInsertedDao.id)
        cartFromDb shouldNotBe null
        cartFromDb?.let { cartMapper.daoToDto(it) } shouldBe CartFactory.create(cartId)
        val createCartResponseString = mockMvc
          .perform(
            MockMvcRequestBuilders.delete("/client/v1/cart/$cartId")
          )
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString
        genericRepository.findOne(insertedCartDao::id eq cartToBeInsertedDao.id) shouldBe null
      }
    }
  }

  private fun assertCreateCartResponse(createCartResponse: CartResponseDtoV0, cartToBeCreated: CartDtoV0) {
    createCartResponse.context shouldNotBe null
    createCartResponse.message shouldNotBe null
    createCartResponse.message?.cart?.id shouldNotBe null
    val expectedCartDto = cartToBeCreated.copy(id = createCartResponse.message?.cart?.id)
    createCartResponse.message?.cart shouldBe expectedCartDto
  }

  private fun assertCartIsPersistedInDb(createCartResponse: CartResponseDtoV0, cartDto: CartDtoV0) {
    val cartFromDb = genericRepository.findOne(CartDao::id eq createCartResponse.message?.cart?.id)
    cartFromDb shouldNotBe null
    val expectedCartDto = cartDto.copy(id = createCartResponse.message?.cart?.id)
    cartFromDb?.let { cartMapper.daoToDto(it) } shouldBe expectedCartDto
  }

  private fun invokeCartCreateOrUpdateApi(cart: CartDtoV0) = mockMvc
    .perform(
      put("/client/v1/cart")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(cart))
    )

}
