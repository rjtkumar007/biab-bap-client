package org.beckn.one.sandbox.bap.services

import arrow.core.flatMap
import org.beckn.one.sandbox.bap.dtos.BecknResponse
import org.beckn.one.sandbox.bap.dtos.Context
import org.beckn.one.sandbox.bap.dtos.ResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class SearchService(
  @Autowired val registryService: RegistryService,
  @Autowired val gatewayService: GatewayService
) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun search(queryString: String, context: Context): ResponseEntity<BecknResponse> {
    return registryService
      .lookupGateways()
      .flatMap { gatewayService.search(it.first(), queryString) }
      .fold(
        {
          log.error("Error when initiating search. Error: {}", it)
          ResponseEntity
            .status(it.status().value())
            .body(
              BecknResponse(
                context = context,
                message = it.message(),
                error = it.error()
              )
            )
        },
        {
          log.info("Found gateways: {}", it)
          ResponseEntity
            .ok(
              BecknResponse(
                context = context,
                message = ResponseMessage.ack()
              )
            )
        }
      )
  }
}
