package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.ClientErrorResponse
import org.beckn.one.sandbox.bap.client.dtos.ListWrapperResponseMessage
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.schemas.ProtocolOnConfirm
import org.beckn.one.sandbox.bap.schemas.ProtocolOnInit
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSelect
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CallbacksController @Autowired constructor(
  val initStore: ResponseStorageService<ProtocolOnInit>,
  val selectStore: ResponseStorageService<ProtocolOnSelect>,
  val confirmStore: ResponseStorageService<ProtocolOnConfirm>,
  val contextFactory: ContextFactory
) {

  @RequestMapping("/client/v1/on_init")
  @ResponseBody
  fun onInitV1(
    @RequestParam messageId: String
  ) = initStore.findByMessageId(messageId)
    .fold(
      {
        ResponseEntity
          .status(it.status().value())
          .body(ClientErrorResponse(context = contextFactory.create(), error = it.error()))
      },
      {
        ResponseEntity.ok(ListWrapperResponseMessage(it))
      }
    )

  @RequestMapping("/client/v1/on_select")
  @ResponseBody
  fun onSelectV1(
    @RequestParam messageId: String
  ) = selectStore.findByMessageId(messageId).fold(
    {
      ResponseEntity
        .status(it.status().value())
        .body(ClientErrorResponse(context = contextFactory.create(), error = it.error()))
    },
    {
      ResponseEntity.ok(ListWrapperResponseMessage(it))
    }
  )

  @RequestMapping("/client/v1/on_confirm")
  @ResponseBody
  fun onConfirmV1(
    @RequestParam messageId: String
  ) = confirmStore.findByMessageId(messageId).fold(
    {
      ResponseEntity
        .status(it.status().value())
        .body(ClientErrorResponse(context = contextFactory.create(), error = it.error()))
    },
    {
      ResponseEntity.ok(ListWrapperResponseMessage(it))
    }
  )
}