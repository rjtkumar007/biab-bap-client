package org.beckn.one.sandbox.bap.client.shared.dtos

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.errors.TrackError
import org.beckn.protocol.schemas.Default

data class RatingRequestDto @Default constructor(
  val context: ClientContext,
  val message: RatingRequestMessage
) {
  fun validate(): Either<TrackError, RatingRequestDto> =
    when (context.bppId) {
      null -> Either.Left(TrackError.BppIdNotPresent)
      else -> Either.Right(this)
    }
}

data class RatingRequestMessage @Default constructor(
  val refId: String,
  val value: Int
)