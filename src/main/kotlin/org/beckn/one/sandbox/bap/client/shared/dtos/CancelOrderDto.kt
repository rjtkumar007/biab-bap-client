package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.protocol.schemas.Default

data class CancelOrderDto @Default constructor(
  val context: ClientContext,
  val message: CancelOrderRequestMessage
)

data class CancelOrderRequestMessage @Default constructor(
  val orderId: String,
  val cancellationReasonId: String
)