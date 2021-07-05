package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar

data class CreateCartResponse(
  override val context: ProtocolContext,
  val message: CartResponseMessage,
  override val error: ProtocolError? = null,
) : ClientResponse

data class GetCartResponse(
  override val context: ProtocolContext,
  val message: CartResponseMessage,
  override val error: ProtocolError? = null,
) : ClientResponse

data class CartResponseMessage(
  val cart: Cart,
)

data class Cart(
  val id: String? = null,
  val items: List<CartItem>? = null
)

data class CartItem(
  val bppId: String,
  val provider: CartItemProvider,
  val itemId: String,
  val quantity: Int,
  val measure: ProtocolScalar? = null
)

data class CartItemProvider(
  val id: String,
  val providerLocations: List<String>? = null
)
