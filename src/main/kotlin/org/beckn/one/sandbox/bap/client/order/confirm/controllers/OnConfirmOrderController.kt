package org.beckn.one.sandbox.bap.client.order.confirm.controllers

import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.order.confirm.services.OnConfirmOrderService
import org.beckn.one.sandbox.bap.client.shared.controllers.AbstractOnPollController
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientConfirmResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientErrorResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderResponse
import org.beckn.one.sandbox.bap.client.shared.errors.ClientError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.mappers.OnBapEntityToDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.protocol.schemas.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnConfirmOrderController @Autowired constructor(
    onPollService: GenericOnPollService<ProtocolOnConfirm, ClientConfirmResponse>,
    val contextFactory: ContextFactory,
    val protocolClient: ProtocolClient,
    val repository: ResponseStorageService<OrderResponse,OrderDao>,
    val mapping: OnBapEntityToDao,
    val onConfirmOrderService: OnConfirmOrderService
) : AbstractOnPollController<ProtocolOnConfirm, ClientConfirmResponse>(onPollService, contextFactory) {

  @RequestMapping("/client/v1/on_confirm_order")
  @ResponseBody
  fun onConfirmOrderV1(
    @RequestParam messageId: String
  ): ResponseEntity<out ClientResponse> {
    val user = SecurityUtil.getSecuredUserDetail()
    val bapResult = onPoll(messageId, protocolClient.getConfirmResponsesCall(messageId))
    when (bapResult.statusCode.value()) {
      200 -> {
        if(user != null){
          val resultResponse: ClientConfirmResponse = bapResult.body as ClientConfirmResponse
          if(resultResponse.message != null){
            val orderDao :OrderDao = mapping.entityToDao(resultResponse?.message?.order!!)
            orderDao.transactionId = "75nm6996-69b5-108a-b57e-298e130eb112" //resultResponse.context.transactionId
            orderDao.userId =user.uid
            orderDao.messageId =messageId
            onConfirmOrderService.updateOrder(orderDao).fold(
              {
                return mapToErrorResponse( it,context = contextFactory.create(messageId = messageId))
              }, {
                return bapResult
              }
            )
          }else{
            return mapToErrorResponse(ClientError.AuthenticationError,context = contextFactory.create(messageId = messageId))
          }
          }else{
            //token expired
          return bapResult
        }

      }
      else -> {
        return bapResult
      }
    }
  }

  private fun mapToErrorResponse(it: HttpError, context: ProtocolContext) = ResponseEntity
    .status(it.status())
    .body(
      ClientErrorResponse(
        context = context,
        error = it.error()
      )
    )
}