package org.beckn.one.sandbox.bap.protocol.controllers

import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.schemas.ProtocolAckResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolResponse
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

open class BaseProtocolController<Protocol: ProtocolResponse> @Autowired constructor(
  private val storage: ResponseStorageService<Protocol>
) {
  val log = LoggerFactory.getLogger(BaseProtocolController::class.java)

  fun onCallback(@RequestBody searchResponse: Protocol) = storage
    .save(searchResponse)
    .fold(
      ifLeft = {
        log.error("Error during persisting. Error: {}", it)
        ResponseEntity
          .status(it.status().value())
          .body(ProtocolAckResponse(searchResponse.context, it.message(), it.error()))
      },
      ifRight = {
        log.info("Successfully persisted response with message id: ${searchResponse.context?.messageId}")
        ResponseEntity.ok(ProtocolAckResponse(searchResponse.context, ResponseMessage.ack()))
      }
    )
}