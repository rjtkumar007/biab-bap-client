package org.beckn.one.sandbox.bap.client.order.status.services

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import org.beckn.one.sandbox.bap.client.external.hasBody
import org.beckn.one.sandbox.bap.client.external.isAckNegative
import org.beckn.one.sandbox.bap.client.external.isInternalServerError
import org.beckn.one.sandbox.bap.client.external.provider.BppClientFactory
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOrderStatusRequest
import org.beckn.protocol.schemas.ProtocolOrderStatusRequestMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class BppOrderStatusService @Autowired constructor(
  private val bppServiceClientFactory: BppClientFactory
) {
  private val log: Logger = LoggerFactory.getLogger(BppOrderStatusService::class.java)

  fun getOrderStatus(bppUri: String, context: ProtocolContext, message: ProtocolOrderStatusRequestMessage):
      Either<BppError, ProtocolAckResponse> = Either.catch {
    log.info("Invoking Order Status API on BPP: {}", bppUri)
    val bppServiceClient = bppServiceClientFactory.getClient(bppUri)
    val httpResponse =
      bppServiceClient.getOrderStatus(ProtocolOrderStatusRequest(context = context, message = message))
        .execute()
    log.info("BPP Get Order Status API response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
    return when {
      httpResponse.isInternalServerError() -> Left(BppError.Internal)
      !httpResponse.hasBody() -> Left(BppError.NullResponse)
      httpResponse.isAckNegative() -> Left(BppError.Nack)
      else -> Right(httpResponse.body()!!)
    }
  }.mapLeft {
    log.error("Error when invoking BPP Get Order Status API", it)
    BppError.Internal
  }
}