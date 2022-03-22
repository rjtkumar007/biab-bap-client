package org.beckn.one.sandbox.bap.client.policy.mappers

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyResponseMessage
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollMapper
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnCancellationReasons

class OnCancellationReasonsResponseMapper : GenericOnPollMapper<ProtocolOnCancellationReasons, ClientOrderPolicyResponse> {
  override fun transform(
    input: List<ProtocolOnCancellationReasons>,
    context: ProtocolContext
  ): Either<HttpError, ClientOrderPolicyResponse> =
    Either.Right(
      ClientOrderPolicyResponse(
        context = context,
        message = ClientOrderPolicyResponseMessage(
          cancellationReasons = input.firstOrNull()?.message?.cancellationReasons,
          ratingCategories = input.firstOrNull()?.message?.ratingCategories,
        ),
        error = input.firstOrNull()?.error
      )
    )
}