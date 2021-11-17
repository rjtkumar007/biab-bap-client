package org.beckn.one.sandbox.bap.client.fulfillment.track.controllers

import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.shared.controllers.AbstractOnPollController
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientErrorResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderStatusResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientTrackResponse
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOnTrack
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class OnTrackPollController(
    onPollService: GenericOnPollService<ProtocolOnTrack, ClientTrackResponse>,
    val contextFactory: ContextFactory,
    val protocolClient: ProtocolClient
) : AbstractOnPollController<ProtocolOnTrack, ClientTrackResponse>(onPollService, contextFactory) {

  @RequestMapping(value = ["/client/v1/on_track"],method = [RequestMethod.GET])
  @ResponseBody
  fun onTrack(@RequestParam messageId: String): ResponseEntity<out ClientResponse> =
    onPoll(messageId, protocolClient.getTrackResponsesCall(messageId))

  @RequestMapping(value = ["/client/v2/on_track"],method = [RequestMethod.GET])
  @ResponseBody
  fun onTrackV2(@RequestParam messageIds: String): ResponseEntity<out List<ClientResponse>> {
    if (messageIds.isNotEmpty() && messageIds.trim().isNotEmpty()) {
      val messageIdArray = messageIds.split(",")
      var okResponseOnSupport: MutableList<ClientResponse> = ArrayList()

      for (messageId in messageIdArray) {
        val bapResult = onPoll(messageId, protocolClient.getTrackResponsesCall(messageId))
        when (bapResult.statusCode.value()) {
          200 -> {
            val resultResponse = bapResult.body as ClientOrderStatusResponse
            okResponseOnSupport.add(resultResponse)
          }
          else -> {
            okResponseOnSupport.add(
              ClientErrorResponse(
                context = contextFactory.create(messageId = messageId),
                error = bapResult.body?.error
              )
            )
          }
        }
      }
      return ResponseEntity.ok(okResponseOnSupport)
    } else {
      return mapToErrorResponse(BppError.BadRequestError)
    }
  }

  private fun mapToErrorResponse(it: HttpError, context: ProtocolContext? = null) = ResponseEntity
    .status(it.status())
    .body(
      listOf(
        ClientErrorResponse(
          context = context,
          error = it.error()
        )
      )
    )
}
