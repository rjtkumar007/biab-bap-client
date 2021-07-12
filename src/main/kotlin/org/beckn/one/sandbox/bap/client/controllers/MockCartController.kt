package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.*
import org.beckn.one.sandbox.bap.schemas.*
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
class MockCartController @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val uuidFactory: UuidFactory
) {

  @PutMapping("/client/v0/cart")
  @ResponseBody
  fun createOrUpdateCart(@RequestBody cart: CartDto): ResponseEntity<ProtocolAckResponse> {
    return ResponseEntity.ok(
      ProtocolAckResponse(
        context = getContext(),
        message = ResponseMessage.ack()
      )
    )
  }

  @GetMapping("/client/v0/on_cart")
  @ResponseBody
  fun onCart(@RequestParam(name = "message_id") messageId: String): ResponseEntity<CartResponseDto> {
    return ResponseEntity.ok(
      CartResponseDto(
        context = getContext(messageId = messageId),
        message = CartResponseMessageDto(cart = buildCart("cart 1"))
      )
    )
  }

  @GetMapping("/client/v0/cart/{id}")
  @ResponseBody
  fun getCart(@PathVariable id: String): ResponseEntity<CartResponseDto> {
    return ResponseEntity.ok(
      CartResponseDto(
        context = getContext(),
        message = CartResponseMessageDto(cart = buildCart(id))
      )
    )
  }

  private fun getContext(messageId: String = uuidFactory.create()) =
    contextFactory.create(action = null, messageId = messageId)

  private fun buildCart(id: String?) = CartDto(
    id = id,
    transactionId = "ac99a617-5065-4ae8-9695-d9de3d80f030",
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
        bppId = "local-coffee-house",
        bppUri = "www.local-coffee-house.in",
        provider = CartItemProviderDto(
          id = "venugopala stores",
          locations = listOf("13.001581,77.5703686")
        ),
        quantity = CartSelectedItemQuantity(
          count = 1,
          measure = ProtocolScalar(
            value = BigDecimal.valueOf(1),
            unit = "kg"
          )
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
        bppId = "local-coffee-house",
        bppUri = "www.local-coffee-house.in",
        provider = CartItemProviderDto(
          id = "venugopala stores",
          locations = listOf("13.001581,77.5703686")
        ),
        quantity = CartSelectedItemQuantity(
          count = 1,
          measure = ProtocolScalar(
            value = BigDecimal.valueOf(500),
            unit = "gm"
          )
        ),
      ),
    )
  )
}