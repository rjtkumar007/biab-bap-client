package org.beckn.one.sandbox.bap.client.order.cancel.controllers

import org.beckn.one.sandbox.bap.client.order.cancel.services.CancelOrderService
import org.beckn.one.sandbox.bap.client.shared.dtos.CancelOrderDto
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CancelOrderController @Autowired constructor(
    private val contextFactory: ContextFactory,
    private val cancelOrderService: CancelOrderService
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/cancel_order")
  @ResponseBody
  fun cancelOrderV1(@RequestBody request: CancelOrderDto): ResponseEntity<ProtocolAckResponse> {
    log.info("Got request to cancel order")
    val context = getContext(request.context.transactionId, request.context.bppId)
    return cancelOrderService.cancel(
      context = context,
      orderId = request.message.orderId,
      cancellationReasonId = request.message.cancellationReasonId
    ).fold(
      {
        log.error("Error when cancelling order with BPP: {}", it)
        mapToErrorResponse(it, context)
      },
      {
        log.info("Successfully cancelled order with BPP. Message: {}", it)
        ResponseEntity.ok(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
      }
    )
  }

  private fun mapToErrorResponse(it: HttpError, context: ProtocolContext) = ResponseEntity
    .status(it.status())
    .body(
      ProtocolAckResponse(
        context = context,
        message = it.message(),
        error = it.error()
      )
    )

  private fun getContext(transactionId: String, bppId: String? = null) =
    contextFactory.create(action = ProtocolContext.Action.CANCEL, transactionId = transactionId, bppId = bppId)
}