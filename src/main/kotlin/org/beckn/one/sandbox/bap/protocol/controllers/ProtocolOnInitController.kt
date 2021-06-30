package org.beckn.one.sandbox.bap.protocol.controllers

import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSelect
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ProtocolOnSelectController(
  store: ResponseStorageService<ProtocolOnSelect>
): BaseProtocolController<ProtocolOnSelect>(store) {

  @PostMapping(
    "v1/on_select",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE],
  )
  fun onSelect(@RequestBody selectResponse: ProtocolOnSelect) = onCallback(selectResponse)

}