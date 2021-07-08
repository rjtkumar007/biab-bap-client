package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.daos.CartDao
import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.factories.CartFactory
import org.beckn.one.sandbox.bap.client.mappers.CartMapper
import org.beckn.one.sandbox.bap.message.entities.MessageDao
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.schemas.ProtocolAckResponse
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class CartControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val uuidFactory: UuidFactory,
  val genericRepository: GenericRepository<CartDao>,
  val cartMapper: CartMapper,
  val messageRepository: GenericRepository<MessageDao>
) : DescribeSpec() {
  init {
    describe("Cart") {

      it("should create cart if it does not exist") {
        val cart = CartFactory.create(null)

        val createCartResponseString = invokeCartCreateOrUpdateApi(cart)
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val createCartResponse = objectMapper.readValue(createCartResponseString, ProtocolAckResponse::class.java)
        createCartResponse.context shouldNotBe null
        createCartResponse.context.messageId shouldNotBe null
        createCartResponse.message shouldBe ResponseMessage.ack()

        val savedMessage = messageRepository.findOne(MessageDao::id eq createCartResponse.context.messageId)
        savedMessage shouldNotBe null
        savedMessage?.id shouldBe createCartResponse.context.messageId
        savedMessage?.type shouldBe MessageDao.Type.Select
      }
    }
  }

  private fun invokeCartCreateOrUpdateApi(cart: CartDto) = mockMvc
    .perform(
      put("/client/v1/cart")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(cart))
    )

}
