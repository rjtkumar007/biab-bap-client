package org.beckn.one.sandbox.bap.client.rating.mappers

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientRatingResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientRatingResponseMessage
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollMapper
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnRating

class RatingClientResponseMapper : GenericOnPollMapper<ProtocolOnRating, ClientRatingResponse> {
  override fun transform(
    input: List<ProtocolOnRating>,
    context: ProtocolContext
  ): Either<HttpError, ClientRatingResponse> =
    Either.Right(
      ClientRatingResponse(
        context = context,
        message = ClientRatingResponseMessage(feedback = input.firstOrNull()?.message?.feedback),
        error = input.firstOrNull()?.error
      )
    )
}