package org.beckn.one.sandbox.bap.controllers

import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.dtos.ResponseMessage
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.services.SearchService
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
  val contextFactory: ContextFactory,
) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  @RequestMapping("/v1/search")
  @ResponseBody
  fun searchV1(@RequestParam searchString: String): ResponseEntity<Response> {
    log.info("Got search request: {}", searchString)
    return searchService.search(contextFactory.create(), searchString)
  }

  @RequestMapping("/v0/search")
  @ResponseBody
  fun searchV0(@RequestParam(required = false) searchString: String? = ""): ResponseEntity<Response> {
    return ResponseEntity.ok(Response(contextFactory.create(), ResponseMessage.ack()))
  }
}