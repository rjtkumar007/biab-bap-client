package org.beckn.one.sandbox.bap.client.order.confirm.controllers

import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.order.confirm.services.ConfirmOrderService
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderRequestDto
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.message.entities.BecknResponseDao
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ResponseMessage
import org.litote.kmongo.eq
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@RestController
class ConfirmOrderController @Autowired constructor(
    private val contextFactory: ContextFactory,
    private val confirmOrderService: ConfirmOrderService,
    private  val confirmOrderRepository :ResponseStorageService<OrderResponse,OrderDao>
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/confirm_order")
  @ResponseBody
  fun confirmOrderV1(
    @RequestBody orderRequest: OrderRequestDto
  ): ResponseEntity<ProtocolAckResponse> {

    val context = getContext(orderRequest.context.transactionId)
    return confirmOrderService.confirmOrder(
      context = context,
      order = orderRequest.message
    )
      .fold(
        {
          log.error("Error when confirming order: {}", it)
          mapToErrorResponse(it, context)
        },
        {
          log.info("Successfully confirmed order. Message: {}", it)
          if(SecurityUtil.getSecuredUserDetail() != null){
            confirmOrderRepository.updateDocByQuery(
              OrderDao::messageId eq context?.messageId,
              OrderDao(userId = SecurityUtil.getSecuredUserDetail()?.uid, messageId = context?.messageId,
              transactionId = null))
              .fold(
                {
                  log.error("Error when updating order: {}", it)
                  mapToErrorResponse(it,context)
                },
                {
                  log.info("Successfully updated  user : {}", it)
                  ResponseEntity.ok(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
                }
              )
          }else{
            mapToErrorResponse(BppError.AuthenticationError,context)
          }
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

  private fun getContext(transactionId: String) =
    contextFactory.create(action = ProtocolContext.Action.CONFIRM, transactionId = transactionId)
}