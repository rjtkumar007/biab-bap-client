package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.Default
import org.beckn.one.sandbox.bap.schemas.ProtocolBilling
import org.beckn.one.sandbox.bap.schemas.ProtocolItemQuantityAllocated

data class OrderDto @Default constructor(
  val id: String? = null,
  val transactionId: String,
  val items: List<OrderItemDto>? = null,
  val deliveryInfo: DeliveryInfoDto,
  val billingInfo: ProtocolBilling
)

data class OrderItemDto @Default constructor(
  val id: String,
  val bppId: String,
  val quantity: ProtocolItemQuantityAllocated,
  val provider: CartItemProviderDto
) {
}