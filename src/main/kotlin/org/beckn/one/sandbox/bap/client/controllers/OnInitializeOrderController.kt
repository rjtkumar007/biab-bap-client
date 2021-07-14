package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.ClientInitResponse
import org.beckn.one.sandbox.bap.client.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.services.GenericOnPollService
import org.beckn.one.sandbox.bap.schemas.ProtocolOnInit
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class OnInitializeOrderController @Autowired constructor(
  @Qualifier("InitResults")onPollService: GenericOnPollService<ProtocolOnInit, ClientInitResponse>,
  contextFactory: ContextFactory
) : BaseOnPollController<ProtocolOnInit, ClientInitResponse>(onPollService, contextFactory) {

  @PostMapping("/client/v1/on_initialize_order")
  @ResponseBody
  fun onInitializeOrderV1(
    @RequestBody orderRequest: ProtocolOnInit
  ): ResponseEntity<out ClientResponse> = onPoll(orderRequest.context.messageId)
}