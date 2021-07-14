package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.Default
import org.beckn.one.sandbox.bap.schemas.ProtocolDescriptor
import org.beckn.one.sandbox.bap.schemas.ProtocolPrice
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import org.beckn.one.sandbox.bap.schemas.ProtocolTime

data class CartDto @Default constructor(
  val id: String? = null,
  val transactionId: String,
  val items: List<CartItemDto>? = null
)

data class CartItemDto @Default constructor(
  val id: String,
  val parentItemId: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val price: ProtocolPrice? = null,
  val categoryId: String? = null,
  val locationId: String? = null,
  val time: ProtocolTime? = null,
  val tags: Map<String, String>? = null,
  val bppId: String,
  val provider: CartItemProviderDto,
  val quantity: CartSelectedItemQuantity
)

data class CartSelectedItemQuantity @Default constructor(
  val count: Int,
  val measure: ProtocolScalar? = null
)

data class CartItemProviderDto @Default constructor(
  val id: String,
  val locations: List<String>? = null
)
