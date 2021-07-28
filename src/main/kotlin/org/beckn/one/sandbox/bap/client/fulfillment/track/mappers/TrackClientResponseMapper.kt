package org.beckn.one.sandbox.bap.client.fulfillment.track.mappers

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientTrackResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientTrackResponseMessage
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollMapper
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnTrack

class TrackClientResponseMapper : GenericOnPollMapper<ProtocolOnTrack, ClientTrackResponse> {
  override fun transform(
    input: List<ProtocolOnTrack>,
    context: ProtocolContext
  ): Either<HttpError, ClientTrackResponse> =
    Either.Right(
      ClientTrackResponse(
        context = context,
        message = ClientTrackResponseMessage(tracking = input.firstOrNull()?.message?.tracking),
        error = input.firstOrNull()?.error
      )
    )
}