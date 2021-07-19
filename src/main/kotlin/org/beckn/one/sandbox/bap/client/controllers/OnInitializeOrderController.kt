package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.ClientInitResponse
import org.beckn.one.sandbox.bap.client.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.services.GenericOnPollService
import org.beckn.protocol.schemas.ProtocolOnInit
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnInitializeOrderController @Autowired constructor(
  @Qualifier("InitResults")onPollService: GenericOnPollService<ProtocolOnInit, ClientInitResponse>,
  contextFactory: ContextFactory
) : BaseOnPollController<ProtocolOnInit, ClientInitResponse>(onPollService, contextFactory) {

  @RequestMapping("/client/v1/on_initialize_order")
  @ResponseBody
  fun onInitializeOrderV1(
    @RequestParam messageId: String
  ): ResponseEntity<out ClientResponse> = onPoll(messageId)
}