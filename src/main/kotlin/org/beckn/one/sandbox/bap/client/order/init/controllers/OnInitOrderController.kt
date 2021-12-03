package org.beckn.one.sandbox.bap.client.order.init.controllers

import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.shared.controllers.AbstractOnPollController
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientInitResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientQuoteResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.protocol.schemas.ProtocolOnInit
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnInitOrderController @Autowired constructor(
  val onPollService: GenericOnPollService<ProtocolOnInit, ClientInitResponse>,
  val contextFactory: ContextFactory,
  val protocolClient: ProtocolClient
) : AbstractOnPollController<ProtocolOnInit, ClientInitResponse>(onPollService, contextFactory) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @RequestMapping("/client/v1/on_initialize_order")
  @ResponseBody
  fun onInitOrderV1(
    @RequestParam messageId: String
  ): ResponseEntity<out ClientResponse> = onPoll(messageId, protocolClient.getInitResponsesCall(messageId))


  @RequestMapping("/client/v2/on_initialize_order")
  @ResponseBody
  fun onInitOrderV2(
    @RequestParam messageIds: String
  ): ResponseEntity<out List<ClientResponse>>
  {

    if (messageIds.isNotEmpty() && messageIds.trim().isNotEmpty()) {
      val messageIdArray = messageIds.split(",")
      var okResponseInit: MutableList<ClientInitResponse> = ArrayList()

        for (messageId in messageIdArray) {
          onPollService.onPoll(contextFactory.create(messageId = messageId),
            protocolClient.getInitResponsesCall(messageId))
            .fold(
            {
              okResponseInit.add(
                ClientInitResponse(
                  context = contextFactory.create(messageId = messageId),
                  error = it.error()
                )
              )
            }, {
              okResponseInit.add(it)
            }
          )
        }
        log.info("`Initiated and returning onInit acknowledgment`. Message: {}", okResponseInit)
        return ResponseEntity.ok(okResponseInit)
      } else {
        return mapToErrorResponse(BppError.BadRequestError)
      }
    }

  private fun mapToErrorResponse(it: HttpError) = ResponseEntity
    .status(it.status())
    .body(
      listOf(
        ClientInitResponse(
          error = it.error(),
          context = null
        )
      )
    )
}