package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.services.CartService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.schemas.ProtocolAckResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolContext.Action.SELECT
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CartController @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val cartService: CartService
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PutMapping("/client/v1/cart")
  @ResponseBody
  fun saveCart(@RequestBody cart: CartDto): ResponseEntity<ProtocolAckResponse> {
    val context = getContext(cart.transactionId)
    return cartService.saveCart(context, cart)
      .fold(
        {
          log.error("Error when saving cart: {}", it)
          mapToErrorResponse(it, context)
        },
        {
          log.info("Successfully saved cart. Message: {}", it)
          ResponseEntity.ok(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
        }
      )
  }

  private fun mapToErrorResponse(it: HttpError, context: ProtocolContext) = ResponseEntity
    .status(it.status())
    .body(
      ProtocolAckResponse(
        context = context,
        message = it.message(),
        error = it.error()
      )
    )

  private fun getContext(transactionId: String) = contextFactory.create(action = SELECT, transactionId = transactionId)
}