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
  fun createCart(@RequestBody cart: CartDtoV0): ResponseEntity<CartResponseDtoV0> {
    return ResponseEntity.ok(
      CartResponseDtoV0(
        context = getContext(),
        message = CartResponseMessageDtoV0(cart = cart)
      )
    )
  }

  @GetMapping("/client/v0/cart/{id}")
  @ResponseBody
  fun getCart(@PathVariable id: String): ResponseEntity<CartResponseDtoV0> {
    return ResponseEntity.ok(
      CartResponseDtoV0(
        context = getContext(),
        message = CartResponseMessageDtoV0(cart = buildCart(id))
      )
    )
  }

  @PutMapping("/client/v0/cart/{id}")
  @ResponseBody
  fun updateCart(@PathVariable id: String, @RequestBody updatedCart: CartDtoV0): ResponseEntity<ProtocolAckResponse> {
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

  private fun buildCart(id: String?) = CartDtoV0(
    id = id, items = listOf(
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
      ),
      CartItemDtoV0(
        bppId = "paisool",
        provider = CartItemProviderDtoV0(
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