package org.beckn.one.sandbox.bap.controllers

import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.dtos.ResponseStatus
import org.beckn.one.sandbox.bap.services.SearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class SearchController(@Autowired val searchService: SearchService) {

  @RequestMapping("/v1/search")
  @ResponseBody
  fun searchV1(@RequestParam searchString: String): ResponseEntity<Response> {
    return searchService.search(searchString)
  }

  @RequestMapping("/v0/search")
  @ResponseBody
  fun searchV0(@RequestParam(required = false) searchString: String? = ""): ResponseEntity<Response> {
    return ResponseEntity.ok(Response(status = ResponseStatus.ACK, message_id = UUID.randomUUID().toString()))
  }
}