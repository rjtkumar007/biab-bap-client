package org.beckn.one.sandbox.bap.client.order.cancel.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.external.hasBody
import org.beckn.one.sandbox.bap.client.external.isAckNegative
import org.beckn.one.sandbox.bap.client.external.isInternalServerError
import org.beckn.one.sandbox.bap.client.external.provider.BppClientFactory
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolCancelRequest
import org.beckn.protocol.schemas.ProtocolCancelRequestMessage
import org.beckn.protocol.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BppCancelService @Autowired constructor(
  private val bppServiceClientFactory: BppClientFactory
) {
  private val log: Logger = LoggerFactory.getLogger(BppCancelService::class.java)

  fun cancelOrder(
    bppUri: String,
    context: ProtocolContext,
    orderId: String,
    cancellationReasonId: String
  ): Either<BppError, ProtocolAckResponse> =
    Either.catch {
      log.info("Invoking cancel order API on BPP: {}", bppUri)
      val bppServiceClient = bppServiceClientFactory.getClient(bppUri)
      val httpResponse = bppServiceClient.cancel(
        ProtocolCancelRequest(
          context = context,
          message = ProtocolCancelRequestMessage(
            orderId = orderId,
            cancellationReasonId = cancellationReasonId
          )
        )
      ).execute()
      log.info("BPP cancel order response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      return when {
        httpResponse.isInternalServerError() -> Either.Left(BppError.Internal)
        !httpResponse.hasBody() -> Either.Left(BppError.NullResponse)
        httpResponse.isAckNegative() -> Either.Left(BppError.Nack)
        else -> Either.Right(httpResponse.body()!!)
      }
    }.mapLeft {
      log.error("Error when invoking BPP cancel API", it)
      BppError.Internal
    }
}