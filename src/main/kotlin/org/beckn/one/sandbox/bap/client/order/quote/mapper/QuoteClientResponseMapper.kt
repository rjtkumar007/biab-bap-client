package org.beckn.one.sandbox.bap.client.order.quote.mapper

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientQuoteResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientQuoteResponseMessage
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollMapper
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnSelect

class QuoteClientResponseMapper : GenericOnPollMapper<ProtocolOnSelect, ClientQuoteResponse> {
  override fun transform(
    input: List<ProtocolOnSelect>,
    context: ProtocolContext
  ): Either<HttpError, ClientQuoteResponse>{
    return Either.Right(
      ClientQuoteResponse(
        context = input.firstOrNull()?.context ?: context,
        message = ClientQuoteResponseMessage(quote = input.firstOrNull()?.message?.order),
        error = input.firstOrNull()?.error
      )
    )
  }

}