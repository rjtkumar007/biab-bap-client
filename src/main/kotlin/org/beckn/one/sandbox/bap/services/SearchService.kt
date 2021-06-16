package org.beckn.one.sandbox.bap.services

import arrow.core.flatMap
import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.dtos.ResponseStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class SearchService(
  @Autowired val registryService: RegistryService,
  @Autowired val gatewayService: GatewayService
) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun search(queryString: String): ResponseEntity<Response> {
    return registryService
      .lookupGateways()
      .flatMap { gatewayService.search(it.first(), queryString) }
      .fold(
        {
          log.error("Error when initiating search")
          ResponseEntity
            .status(it.code().value())
            .body(it.response())
        },
        {
          log.info("Found gateways: {}", it)
          ResponseEntity
            .ok(Response(status = ResponseStatus.ACK, message_id = UUID.randomUUID().toString()))
        }
      )
  }

}
