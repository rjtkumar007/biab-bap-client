package org.beckn.one.sandbox.bap.client.shared.dtos

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.protocol.schemas.Default
import org.beckn.protocol.schemas.ProtocolOrderStatusRequestMessage

data class OrderStatusDto @Default constructor(
  val context: ClientContext,
  val message: ProtocolOrderStatusRequestMessage
) {
  fun validate(): Either<BppError, OrderStatusDto> =
    when (context.bppId) {
      null -> BppError.BppIdNotPresent.left()
      else -> this.right()
    }
}
