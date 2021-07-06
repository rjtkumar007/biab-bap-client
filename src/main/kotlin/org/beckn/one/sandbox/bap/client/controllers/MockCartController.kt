package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.*
import org.beckn.one.sandbox.bap.schemas.ProtocolAckResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
class MockCartController @Autowired constructor(
  private val contextFactory: ContextFactory
) {

  @PostMapping("/client/v0/cart")
  @ResponseBody
  fun createCart(@RequestBody cart: CartDto): ResponseEntity<CartResponseDto> {
    return ResponseEntity.ok(
      CartResponseDto(
        context = getContext(),
        message = CartResponseMessageDto(cart = cart)
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

  @PutMapping("/client/v0/cart/{id}")
  @ResponseBody
  fun updateCart(@PathVariable id: String, @RequestBody updatedCart: CartDto): ResponseEntity<ProtocolAckResponse> {
    return ResponseEntity.ok(
      ProtocolAckResponse(
        context = getContext(),
        message = ResponseMessage.ack()
      )
    )
  }

  @DeleteMapping("/client/v0/cart/{id}")
  @ResponseBody
  fun deleteCart(@PathVariable id: String): ResponseEntity<ProtocolAckResponse> {
    return ResponseEntity.ok(
      ProtocolAckResponse(
        context = getContext(),
        message = ResponseMessage.ack()
      )
    )
  }

  private fun getContext() = contextFactory.create(action = null)

  private fun buildCart(id: String?) = CartDto(
    id = id, items = listOf(
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