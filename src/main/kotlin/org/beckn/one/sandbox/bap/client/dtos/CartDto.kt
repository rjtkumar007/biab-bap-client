package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.Default
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar

data class CartDto @Default constructor(
  val transactionId: String,
  val items: List<CartItemDto>? = null
)

data class CartItemDto @Default constructor(
  val id: String,
  val quantity: CartSelectedItemQuantity,
  val bppId: String,
  val provider: CartItemProviderDto,
)

data class CartSelectedItemQuantity @Default constructor(
  val count: Int,
  val measure: ProtocolScalar? = null
)

data class CartItemProviderDto @Default constructor(
  val id: String,
  val locations: List<String>? = null
)
