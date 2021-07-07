package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.CartDtoV0
import org.beckn.one.sandbox.bap.client.dtos.CartResponseDtoV0
import org.beckn.one.sandbox.bap.client.dtos.DeleteCartResponseDtoV0
import org.beckn.one.sandbox.bap.client.services.CartService
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class CartController @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val cartService: CartService
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PutMapping("/client/v1/cart")
  @ResponseBody
  fun saveCart(@RequestBody cart: CartDtoV0): ResponseEntity<CartResponseDtoV0> {
    val context = getContext()
    return cartService.saveCart(context, cart)
      .fold({
        ResponseEntity
          .status(it.status())
          .body(CartResponseDtoV0(context = context, error = it.error()))
      }, {
        ResponseEntity.ok(it)
      })
  }

  @DeleteMapping("/client/v1/cart/{id}")
  @ResponseBody
  fun deleteCart(@PathVariable id: String): ResponseEntity<DeleteCartResponseDtoV0> {
    val context = getContext()
    return cartService
      .deleteCart(context, id)
      .fold(
        {
          log.error("Error during cart delete. Error: {}", it)
          ResponseEntity
            .status(it.status().value())
            .body(DeleteCartResponseDtoV0(context = context, it.error()))
        },
        {
          log.info("Successfully deleted cart")
          ResponseEntity.ok(DeleteCartResponseDtoV0(context = context))
        })
  }

  private fun getContext() = contextFactory.create(action = null)
}