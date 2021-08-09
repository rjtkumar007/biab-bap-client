package org.beckn.one.sandbox.bap.client.order.cancel.mappers

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientCancelResponse
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollMapper
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnCancel

class CancelClientResponseMapper : GenericOnPollMapper<ProtocolOnCancel, ClientCancelResponse> {
  override fun transform(
    input: List<ProtocolOnCancel>,
    context: ProtocolContext
  ): Either<HttpError, ClientCancelResponse> =
    Either.Right(
      ClientCancelResponse(
        context = context,
        message = input.firstOrNull()?.message,
        error = input.firstOrNull()?.error
      )
    )
}