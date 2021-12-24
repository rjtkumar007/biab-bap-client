package org.beckn.one.sandbox.bap.client.rating.controllers

import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.client.shared.controllers.AbstractOnPollController
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientRatingResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.protocol.schemas.ProtocolOnRating
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class OnRatingPollController(
    onPollService: GenericOnPollService<ProtocolOnRating, ClientRatingResponse>,
    contextFactory: ContextFactory,
    val protocolClient: ProtocolClient
) : AbstractOnPollController<ProtocolOnRating, ClientRatingResponse>(onPollService, contextFactory) {

  @RequestMapping(value = ["/client/v1/on_rating"],method = [RequestMethod.GET])
  @ResponseBody
  fun onRating(@RequestParam messageId: String): ResponseEntity<out ClientResponse> =
    onPoll(messageId, protocolClient.getRatingResponsesCall(messageId))
}
