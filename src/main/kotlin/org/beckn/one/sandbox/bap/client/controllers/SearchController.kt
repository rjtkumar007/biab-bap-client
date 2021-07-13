package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.services.SearchService
import org.beckn.one.sandbox.bap.schemas.ProtocolAckResponse
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController @Autowired constructor(
  val searchService: SearchService,
  val contextFactory: ContextFactory
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @RequestMapping("/client/v1/search")
  @ResponseBody
  fun searchV1(
    @RequestParam(required = false) searchString: String?,
    @RequestParam location: String?,
    @RequestParam providerId: String?,
    @RequestParam categoryId: String?
  ): ResponseEntity<ProtocolAckResponse> {
    val context = contextFactory.create()
    return searchService.search(context, searchString, location, providerId, categoryId)
      .fold(
        {
          log.error("Error during search. Error: {}", it)
          ResponseEntity
            .status(it.status().value())
            .body(ProtocolAckResponse(context, it.message(), it.error()))
        },
        {
          log.info("Successfully initiated Search")
          ResponseEntity.ok(ProtocolAckResponse(context, ResponseMessage.ack()))
        }
      )
  }

  @RequestMapping("/v0/search")
  @ResponseBody
  fun searchV0(
    @RequestParam(required = false) searchString: String,
    @RequestParam location: String?
  ): ResponseEntity<ProtocolAckResponse> {
    log.info(location)
    return ResponseEntity.ok(ProtocolAckResponse(context = contextFactory.create(), message = ResponseMessage.ack()))
  }
}