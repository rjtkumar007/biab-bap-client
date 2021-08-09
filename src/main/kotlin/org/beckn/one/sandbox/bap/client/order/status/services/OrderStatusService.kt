package org.beckn.one.sandbox.bap.client.order.status.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderStatusDto
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrderStatusService @Autowired constructor(
  private val bppOrderStatusService: BppOrderStatusService,
  private val registryService: RegistryService,
) {
  private val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun getOrderStatus(context: ProtocolContext, request: OrderStatusDto): Either<HttpError, ProtocolAckResponse?> {
    log.info("Got get order status request.  Context: {}, Order: {}", context, request)
    return request.validate()
      .flatMap { registryService.lookupBppById(it.context.bppId!!) }
      .flatMap {
        bppOrderStatusService.getOrderStatus(
          context = context,
          bppUri = it.first().subscriber_url,
          message = request.message
        )
      }
  }
}
