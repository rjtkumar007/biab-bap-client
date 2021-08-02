package org.beckn.one.sandbox.bap.client.shared.dtos

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.protocol.schemas.Default

data class RatingRequestDto @Default constructor(
  val context: ClientContext,
  val message: RatingRequestMessage
) {
  fun validate(): Either<BppError, RatingRequestDto> =
    when (context.bppId) {
      null -> Either.Left(BppError.BppIdNotPresent)
      else -> Either.Right(this)
    }
}

data class RatingRequestMessage @Default constructor(
  val refId: String,
  val value: Int
)