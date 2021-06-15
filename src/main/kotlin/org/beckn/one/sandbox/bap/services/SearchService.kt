package org.beckn.one.sandbox.bap.services

import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.dtos.ResponseStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class SearchService(@Autowired val registryService: RegistryService) {
  fun search(): ResponseEntity<Response> {
    return registryService
      .lookupGateways()
      .fold(
        {
          ResponseEntity
            .status(it.code().value())
            .body(it.response())
        },
        {
          ResponseEntity
            .ok(Response(status = ResponseStatus.ACK, message_id = UUID.randomUUID().toString()))
        }
      )
  }

}
