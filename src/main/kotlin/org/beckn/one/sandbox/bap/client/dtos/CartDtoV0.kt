package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.Default
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar

data class CartResponseDtoV0(
  override val context: ProtocolContext,
  val message: CartResponseMessageDtoV0? = null,
  override val error: ProtocolError? = null,
) : ClientResponse

data class DeleteCartResponseDtoV0(
  override val context: ProtocolContext,
  override val error: ProtocolError? = null,
) : ClientResponse

data class CartResponseMessageDtoV0(
  val cart: CartDtoV0,
)

data class CartDtoV0 @Default constructor(
  val id: String? = null,
  val items: List<CartItemDtoV0>? = null
)

data class CartItemDtoV0 @Default constructor(
  val bppId: String,
  val provider: CartItemProviderDtoV0,
  val itemId: String,
  val quantity: Int,
  val measure: ProtocolScalar? = null
)

data class CartItemProviderDtoV0 @Default constructor(
  val id: String,
  val providerLocations: List<String>? = null
)
