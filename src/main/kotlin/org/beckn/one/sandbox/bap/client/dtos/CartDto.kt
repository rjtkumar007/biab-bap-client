package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.Default
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar

data class CartResponseDto(
  override val context: ProtocolContext,
  val message: CartResponseMessageDto? = null,
  override val error: ProtocolError? = null,
) : ClientResponse

data class DeleteCartResponseDto(
  override val context: ProtocolContext,
  override val error: ProtocolError? = null,
) : ClientResponse

data class CartResponseMessageDto(
  val cart: CartDto,
)

data class CartDto @Default constructor(
  val id: String? = null,
  val items: List<CartItemDto>? = null
)

data class CartItemDto @Default constructor(
  val bppId: String,
  val provider: CartItemProviderDto,
  val itemId: String,
  val quantity: Int,
  val measure: ProtocolScalar? = null
)

data class CartItemProviderDto @Default constructor(
  val id: String,
  val providerLocations: List<String>? = null
)
