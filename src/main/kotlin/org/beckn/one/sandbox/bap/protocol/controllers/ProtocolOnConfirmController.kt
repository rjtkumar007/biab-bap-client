package org.beckn.one.sandbox.bap.protocol.controllers

import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.schemas.ProtocolOnConfirm
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ProtocolOnConfirmController(
  store: ResponseStorageService<ProtocolOnConfirm>
): BaseProtocolController<ProtocolOnConfirm>(store) {

  @PostMapping(
    "v1/on_confirm",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun onConfirm(@RequestBody confirmResponse: ProtocolOnConfirm) = onCallback(confirmResponse)

}