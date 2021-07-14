package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.Default
import org.beckn.one.sandbox.bap.schemas.ProtocolBilling
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolItemQuantityAllocated

data class OrderRequestDto @Default constructor(
  val context: ProtocolContext,
  val message: OrderDto
)

data class OrderDto @Default constructor(
  val id: String? = null,
  val items: List<OrderItemDto>? = null,
  val deliveryInfo: DeliveryDto,
  val billingInfo: ProtocolBilling
)

data class OrderItemDto @Default constructor(
  val id: String,
  val bppId: String,
  val quantity: ProtocolItemQuantityAllocated,
  val provider: CartItemProviderDto
) {
}