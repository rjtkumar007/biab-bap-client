package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.dtos.CreateCartResponseDto
import org.beckn.one.sandbox.bap.client.services.CartService
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CartController @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val cartService: CartService
) {

  @PostMapping("/client/v1/cart")
  @ResponseBody
  fun createCart(@RequestBody cart: CartDto): ResponseEntity<CreateCartResponseDto> {
    return ResponseEntity.ok(cartService.saveCart(getContext(), cart))
  }

  private fun getContext() = contextFactory.create(action = null)
}