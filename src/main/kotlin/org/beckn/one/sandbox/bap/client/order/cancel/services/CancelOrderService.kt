package org.beckn.one.sandbox.bap.client.order.cancel.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CancelOrderService @Autowired constructor(
  private val bppService: BppCancelService,
  private val registryService: RegistryService,
  private val log: Logger = LoggerFactory.getLogger(CancelOrderService::class.java)
) {

  fun cancel(
    context: ProtocolContext,
    orderId: String,
    cancellationReasonId: String
  ): Either<HttpError, ProtocolAckResponse?> {
    log.info(
      "Got cancel order request. Context: {}, orderId: {}, cancellationReasonId: {}",
      context,
      orderId,
      cancellationReasonId
    )
    if (context.bppId == null) {
      log.info("BPPId not present")
      return Either.Left(BppError.BppIdNotPresent)
    }
    return registryService.lookupBppById(context.bppId!!).flatMap {
      bppService.cancelOrder(
        bppUri = it.first().subscriber_url,
        context = context,
        orderId = orderId,
        cancellationReasonId = cancellationReasonId
      )
    }
  }
}
