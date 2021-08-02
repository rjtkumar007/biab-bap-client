package org.beckn.one.sandbox.bap.client.shared.dtos

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.beckn.one.sandbox.bap.client.shared.errors.TrackError
import org.beckn.protocol.schemas.Default
import org.beckn.protocol.schemas.ProtocolTrackRequestMessage

data class TrackRequestDto @Default constructor(
  val context: ClientContext,
  val message: ProtocolTrackRequestMessage,
) {
  companion object {
    fun validate(request: TrackRequestDto): Either<TrackError, TrackRequestDto> =
      when (request.context.bppId) {
        null -> TrackError.BppIdNotPresent.left()
        else -> request.right()
      }
  }
}
