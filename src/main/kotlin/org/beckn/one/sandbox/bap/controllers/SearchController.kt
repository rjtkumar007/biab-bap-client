package org.beckn.one.sandbox.bap.controllers

import org.beckn.one.sandbox.bap.controllers.error.Errors
import org.beckn.one.sandbox.bap.controllers.http.HttpStatusCode.INTERNAL_SERVER_ERROR
import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.dtos.ResponseStatus.ACK
import org.beckn.one.sandbox.bap.dtos.ResponseStatus.NACK
import org.beckn.one.sandbox.bap.external.registry.SubscriberDto
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

    val lookupResponse: retrofit2.Response<List<SubscriberDto>> = registryService.lookupGateways()

    val subscribersResponse = lookupResponse.body()
    log.info("Lookup response from gateway, code: {}, body: {}", lookupResponse.code(), subscribersResponse)

    if (lookupResponse.code() == INTERNAL_SERVER_ERROR.code) {
      log.error("Received internal server error response from gateway, returning back error")
      return registryLookupFailedError()
    }

    log.info("Returning ACK response")
    return successfulResponse()
  }

  private fun successfulResponse() =
    ResponseEntity.ok(Response(status = ACK, message_id = UUID.randomUUID().toString()))

  private fun registryLookupFailedError() = ResponseEntity
    .status(INTERNAL_SERVER_ERROR.code)
    .body(Response(status = NACK, message_id = null, error = Errors.REGISTRY_LOOKUP_FAILED_ERROR))

  @PostMapping("/on_search")
  fun postOnSearch(@RequestBody request: Any, uri: UriComponentsBuilder): ResponseEntity<Any> {
    val headers = HttpHeaders()
    return ResponseEntity(headers, HttpStatus.OK)
  }
}