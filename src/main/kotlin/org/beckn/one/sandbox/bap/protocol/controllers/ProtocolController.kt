package org.beckn.one.sandbox.bap.protocol.controllers

import org.beckn.one.sandbox.bap.message.entities.BecknResponse
import org.beckn.one.sandbox.bap.message.entities.SearchResponse
import org.beckn.one.sandbox.bap.message.services.ResponseStoreService
import org.beckn.one.sandbox.bap.schemas.ProtocolAckResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponse
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

open class ProtocolController<Protocol: ProtocolResponse, Entity: BecknResponse> @Autowired constructor(
  private val store: ResponseStoreService<Protocol, Entity>
) {
  val log = LoggerFactory.getLogger(ProtocolController::class.java)

  fun onCallback(@RequestBody searchResponse: Protocol) = store
    .save(searchResponse)
    .fold(
      ifLeft = {
        log.error("Error during persisting. Error: {}", it)
        ResponseEntity
          .status(it.status().value())
          .body(ProtocolAckResponse(searchResponse.context, it.message(), it.error()))
      },
      ifRight = {
        log.info("Successfully persisted response")
        ResponseEntity.ok(ProtocolAckResponse(searchResponse.context, ResponseMessage.ack()))
      }
    )
}

@RestController
@RequestMapping
class ProtocolSearchController(
  store: ResponseStoreService<ProtocolSearchResponse, SearchResponse>
): ProtocolController<ProtocolSearchResponse, SearchResponse>(store) {

  @PostMapping(
    "v1/on_search",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun onSearch(@RequestBody searchResponse: ProtocolSearchResponse) = onCallback(searchResponse)

}