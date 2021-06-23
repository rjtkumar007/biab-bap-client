package org.beckn.one.sandbox.bap.client.services

import arrow.core.flatMap
import org.beckn.one.sandbox.bap.message.entities.Message
import org.beckn.one.sandbox.bap.message.services.MessageService
import org.beckn.one.sandbox.bap.schemas.Context
import org.beckn.one.sandbox.bap.schemas.BecknResponse
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.beckn.one.sandbox.bap.schemas.SearchResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class SearchService(
  @Autowired val registryService: RegistryService,
  @Autowired val gatewayService: GatewayService,
  @Autowired val messageService: MessageService,
) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun search(context: Context, queryString: String?): ResponseEntity<BecknResponse> {
    log.info("Got search request: {}", queryString)
    return registryService
      .lookupGateways()
      .flatMap { gatewayService.search(it.first(), queryString) }
      .flatMap { messageService.save(Message(id = context.messageId, type = Message.Type.Search)) }
      .fold(
        {
          log.error("Error during search. Error: {}", it)
          ResponseEntity
            .status(it.status().value())
            .body(BecknResponse(context, it.message(), it.error()))
        },
        {
          log.info("Successfully initiated Search")
          ResponseEntity.ok(BecknResponse(context, ResponseMessage.ack()))
        }
      )
  }

  fun onSearch(context: Context): ResponseEntity<SearchResponse> {
    log.info("Got on search request for message id: {}", context.messageId)
    return messageService.findById(context.messageId)
      .fold(
        {
          log.error("Error when finding message by id. Error: {}", it)
          ResponseEntity
            .status(it.status().value())
            .body(SearchResponse(context = context, error = it.error()))
        },
        {
          log.info("Successfully initiated Search")
          ResponseEntity
            .ok(SearchResponse(context = context))
        }
      )
  }
}
