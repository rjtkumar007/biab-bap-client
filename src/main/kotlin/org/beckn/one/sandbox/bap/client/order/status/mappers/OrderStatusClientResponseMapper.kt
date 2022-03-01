package org.beckn.one.sandbox.bap.client.order.status.mappers

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderStatusResponse
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollMapper
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnOrderStatus
import org.beckn.protocol.schemas.ProtocolOnOrderStatusMessage

class OrderStatusClientResponseMapper : GenericOnPollMapper<ProtocolOnOrderStatus, ClientOrderStatusResponse> {
  override fun transform(
    input: List<ProtocolOnOrderStatus>,
    context: ProtocolContext
  ): Either<HttpError, ClientOrderStatusResponse> =
    Either.Right(
      ClientOrderStatusResponse(
        context = context,
        message = ProtocolOnOrderStatusMessage(order = input?.sortedByDescending{ it.context?.timestamp }.firstOrNull()?.message?.order),
        error = input.firstOrNull()?.error
      )
    )
}