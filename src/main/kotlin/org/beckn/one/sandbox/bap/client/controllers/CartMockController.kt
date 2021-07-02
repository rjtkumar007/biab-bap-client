package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.Cart
import org.beckn.one.sandbox.bap.client.dtos.CartItem
import org.beckn.one.sandbox.bap.client.dtos.CartItemProvider
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
class CartMockController {
  @RequestMapping("/client/v0/cart")
  @ResponseBody
  fun cartV0(
    @RequestParam id: String?
  ): ResponseEntity<Cart> {
    return ResponseEntity.ok(
      Cart(
        id = id, items = listOf(
          CartItem(
            bppId = "paisool",
            provider = CartItemProvider(
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
          CartItem(
            bppId = "paisool",
            provider = CartItemProvider(
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
    )
  }
}