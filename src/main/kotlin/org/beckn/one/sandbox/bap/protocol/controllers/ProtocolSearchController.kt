package org.beckn.one.sandbox.bap.protocol.controllers

import org.beckn.one.sandbox.bap.message.services.SearchResponseStoreService
import org.beckn.one.sandbox.bap.schemas.ProtocolResponse
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/core/0.9.1-draft03")
class ProtocolSearchController @Autowired constructor(
  val searchResponseStoreService: SearchResponseStoreService
) {
  val log = LoggerFactory.getLogger(ProtocolSearchController::class.java)

  @PostMapping(
    "/on_search",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
  )
  fun onSearch(@RequestBody searchResponse: ProtocolSearchResponse) = searchResponseStoreService
    .save(searchResponse)
    .fold(
      ifLeft = {
        log.error("Error during persisting. Error: {}", it)
        ResponseEntity
          .status(it.status().value())
          .body(ProtocolResponse(searchResponse.context, it.message(), it.error()))
      },
      ifRight = {
        log.info("Successfully persisted response")
        ResponseEntity.ok(ProtocolResponse(searchResponse.context, ResponseMessage.ack()))
      }
    )


}