package org.beckn.one.sandbox.bap.controllers

import org.beckn.one.sandbox.bap.controllers.HttpStatusCode.INTERNAL_SERVER_ERROR
import org.beckn.one.sandbox.bap.dtos.Error
import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.dtos.ResponseStatus.ACK
import org.beckn.one.sandbox.bap.dtos.ResponseStatus.NACK
import org.beckn.one.sandbox.bap.external.registry.RegistryService
import org.beckn.one.sandbox.bap.external.registry.RegistrySubscriber
import org.beckn.one.sandbox.bap.external.registry.SubscriberLookupRequest
import org.beckn.one.sandbox.bap.registry.Subscriber
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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

  @Value("\${context.domain}")
  lateinit var domain: String

  @Value("\${context.city}")
  lateinit var city: String

  @Value("\${context.country}")
  lateinit var country: String

  @RequestMapping("/search")
  @ResponseBody
  fun search(
    @RequestParam searchString: String
  ): ResponseEntity<Response> {
    log.info("Search initiated. 'searchString': {}", searchString)
    val lookupResponse: retrofit2.Response<List<RegistrySubscriber>> = registryService.lookup(
      SubscriberLookupRequest(
        type = Subscriber.Type.BG,
        domain = domain,
        city = city,
        country = country
      )
    ).execute()
    log.info("Lookup response from gateway, code: {}, body: {}", lookupResponse.code(), lookupResponse.body())
    if (lookupResponse.code() == INTERNAL_SERVER_ERROR.code) {
      log.error("Received internal server error response from gateway, returning back error")
      return ResponseEntity
        .status(INTERNAL_SERVER_ERROR.code)
        .body(Response(status = NACK, message_id = null, error = Error("BAP_001", "Registry lookup failed")))
    }
    log.error("Returning ACK response")
    return ResponseEntity.ok(Response(status = ACK, message_id = UUID.randomUUID().toString()))
  }

  @PostMapping("/on_search")
  fun postOnSearch(@RequestBody request: Any, uri: UriComponentsBuilder): ResponseEntity<Any> {
    val headers = HttpHeaders()
    return ResponseEntity(headers, HttpStatus.OK)
  }
}