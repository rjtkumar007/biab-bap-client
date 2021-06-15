package org.beckn.one.sandbox.bap.controllers

import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.dtos.ResponseStatus.ACK
import org.beckn.one.sandbox.bap.services.RegistryService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@RestController
class SearchController {
  val log: Logger = LoggerFactory.getLogger(this.javaClass)

  @Autowired
  lateinit var registryService: RegistryService

  @RequestMapping("/search")
  @ResponseBody
  fun search(@RequestParam searchString: String): ResponseEntity<Response> {
    log.info("Search initiated. 'searchString': {}", searchString)
    return registryService
      .lookupGateways()
      .fold(
        {
          ResponseEntity
            .status(it.code().value())
            .body(it.response())
        },
        {
          successfulResponse()
        }
      )
  }

  private fun successfulResponse() =
    ResponseEntity.ok(Response(status = ACK, message_id = UUID.randomUUID().toString()))

  @PostMapping("/on_search")
  fun postOnSearch(@RequestBody request: Any, uri: UriComponentsBuilder): ResponseEntity<Any> {
    val headers = HttpHeaders()
    return ResponseEntity(headers, HttpStatus.OK)
  }
}