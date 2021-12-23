package org.beckn.one.sandbox.bap.client.order.init.mapper

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientInitResponse
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollMapper
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnInit

class InitClientResponseMapper : GenericOnPollMapper<ProtocolOnInit, ClientInitResponse> {
  override fun transform(
    input: List<ProtocolOnInit>,
    context: ProtocolContext
  ): Either<HttpError, ClientInitResponse> =
    Either.Right(
      ClientInitResponse(
        context = input.firstOrNull()?.context ?: context,
        message = input.firstOrNull()?.message,
        error = input.firstOrNull()?.error
      )
    )
}