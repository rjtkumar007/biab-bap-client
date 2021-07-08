package org.beckn.one.sandbox.bap.client.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.factories.CartFactory
import org.beckn.one.sandbox.bap.common.factories.ResponseFactory
import org.beckn.one.sandbox.bap.message.entities.MessageDao
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.schemas.*
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
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
  val contextFactory: ContextFactory,
  val uuidFactory: UuidFactory,
  val messageRepository: GenericRepository<MessageDao>
) : DescribeSpec() {

  init {
    describe("Save Cart") {
      val providerApi = WireMockServer(4010)
      providerApi.start()
      val cart = CartFactory.create(null, bppUri = providerApi.baseUrl())

      beforeEach {
        providerApi.resetAll()
      }

      it("should return error when bpp select call fails") {
        providerApi.stubFor(post("/select/").willReturn(serverError()))

        val createCartResponseString = invokeCartCreateOrUpdateApi(cart)
          .andExpect(status().isInternalServerError)
          .andReturn()
          .response.contentAsString

        val createCartResponse = verifyResponseMessage(createCartResponseString, cart, ResponseMessage.nack())
        verifyThatMessageWasNotPersisted(createCartResponse)
        verifyThatBppSelectApiWasInvoked(createCartResponse, cart, providerApi)
      }

      it("should invoke provide select api and save message") {
        providerApi
          .stubFor(
            post("/select/").willReturn(
              okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory)))
            )
          )

        val createCartResponseString = invokeCartCreateOrUpdateApi(cart)
          .andExpect(status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val createCartResponse = verifyResponseMessage(createCartResponseString, cart, ResponseMessage.ack())
        verifyThatMessageForSelectRequestIsPersisted(createCartResponse)
        verifyThatBppSelectApiWasInvoked(createCartResponse, cart, providerApi)
      }
    }
  }

  private fun verifyThatMessageWasNotPersisted(createCartResponse: ProtocolAckResponse) {
    val savedMessage = messageRepository.findOne(MessageDao::id eq createCartResponse.context.messageId)
    savedMessage shouldBe null
  }

  private fun verifyResponseMessage(
    createCartResponseString: String,
    cart: CartDto,
    expectedMessage: ResponseMessage
  ): ProtocolAckResponse {
    val createCartResponse = objectMapper.readValue(createCartResponseString, ProtocolAckResponse::class.java)
    createCartResponse.context shouldNotBe null
    createCartResponse.context.messageId shouldNotBe null
    createCartResponse.context.transactionId shouldBe cart.transactionId
    createCartResponse.context.action shouldBe ProtocolContext.Action.SELECT
    createCartResponse.message shouldBe expectedMessage
    return createCartResponse
  }

  private fun verifyThatMessageForSelectRequestIsPersisted(createCartResponse: ProtocolAckResponse) {
    val savedMessage = messageRepository.findOne(MessageDao::id eq createCartResponse.context.messageId)
    savedMessage shouldNotBe null
    savedMessage?.id shouldBe createCartResponse.context.messageId
    savedMessage?.type shouldBe MessageDao.Type.Select
  }

  private fun verifyThatBppSelectApiWasInvoked(
    createCartResponse: ProtocolAckResponse,
    cart: CartDto,
    providerApi: WireMockServer
  ) {
    val protocolSelectRequest = getProtocolSelectRequest(createCartResponse, cart)
    providerApi.verify(
      postRequestedFor(urlEqualTo("/select/"))
        .withRequestBody(equalToJson(objectMapper.writeValueAsString(protocolSelectRequest)))
    )
  }

  private fun getProtocolSelectRequest(createCartResponse: ProtocolAckResponse, cart: CartDto): ProtocolSelectRequest {
    val locations = cart.items?.first()?.provider?.locations?.map { ProtocolLocation(gps = it) }
    return ProtocolSelectRequest(
      context = createCartResponse.context,
      message = ProtocolSelectRequestMessage(
        selected = ProtocolOnSelectMessageSelected(
          provider = ProtocolProvider(
            id = cart.items?.first()?.provider?.id,
            locations = locations
          ),
          providerLocation = locations?.first(),
          items = cart.items?.map {
            ProtocolSelectedItem(
              id = it.id,
              descriptor = it.descriptor,
              price = it.price,
              quantity = ProtocolSelectedItemQuantity(count = it.quantity.count, measure = it.quantity.measure)
            )
          },
        )
      ),
    )
  }

  private fun invokeCartCreateOrUpdateApi(cart: CartDto) = mockMvc
    .perform(
      put("/client/v1/cart")
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .content(objectMapper.writeValueAsString(cart))
    )

}
