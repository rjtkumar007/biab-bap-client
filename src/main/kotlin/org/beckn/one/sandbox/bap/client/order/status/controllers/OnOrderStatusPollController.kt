package org.beckn.one.sandbox.bap.client.order.status.controllers

import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.shared.controllers.AbstractOnPollController
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderStatusResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.protocol.schemas.ProtocolOnOrderStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnOrderStatusPollController(
  onPollService: GenericOnPollService<ProtocolOnOrderStatus, ClientOrderStatusResponse>,
  contextFactory: ContextFactory,
  val protocolClient: ProtocolClient
) : AbstractOnPollController<ProtocolOnOrderStatus, ClientOrderStatusResponse>(onPollService, contextFactory) {

  @RequestMapping("/client/v1/on_order_status")
  @ResponseBody
  fun onOrderStatus(@RequestParam messageId: String): ResponseEntity<out ClientResponse> =
    onPoll(messageId, protocolClient.getOrderStatusResponsesCall(messageId))
}
