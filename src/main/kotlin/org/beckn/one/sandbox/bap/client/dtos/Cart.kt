package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar

data class GetCartResponse(
  override val context: ProtocolContext,
  val message: GetCartResponseMessage? = null,
  override val error: ProtocolError? = null,
) : ClientResponse

data class GetCartResponseMessage(
  val cart: Cart? = null,
)

data class Cart(
  val id: String? = null,
  val items: List<CartItem>? = null
)

data class CartItem(
  val bppId: String? = null,
  val provider: CartItemProvider? = null,
  val itemId: String? = null,
  val quantity: Int? = null,
  val measure: ProtocolScalar? = null
)

data class CartItemProvider(
  val id: String? = null,
  val providerLocations: List<String>? = null
)
