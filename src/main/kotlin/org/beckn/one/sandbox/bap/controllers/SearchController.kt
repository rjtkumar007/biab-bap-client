package org.beckn.one.sandbox.bap.controllers

import org.beckn.one.sandbox.bap.dtos.Ack
import org.beckn.one.sandbox.bap.dtos.AckResponse
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
class SearchController {
  @RequestMapping("/search")
  @ResponseBody
  fun search(): AckResponse {
    return AckResponse(Ack.Status.ACK, UUID.randomUUID().toString())
  }
}