package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either.Right
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.dtos.ClientSearchResponse
import org.beckn.one.sandbox.bap.message.entities.Message
import org.beckn.one.sandbox.bap.message.mappers.CatalogMapper
import org.beckn.one.sandbox.bap.message.services.MessageService
import org.beckn.one.sandbox.bap.message.services.SearchResponseStoreService
import org.beckn.one.sandbox.bap.schemas.ProtocolResponse
import org.beckn.one.sandbox.bap.schemas.Context
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
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
  @Autowired val searchResponseStoreService: SearchResponseStoreService,
  @Autowired val catalogMapper: CatalogMapper,
) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun search(context: Context, queryString: String?): ResponseEntity<ProtocolResponse> {
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
            .body(ProtocolResponse(context, it.message(), it.error()))
        },
        {
          log.info("Successfully initiated Search")
          ResponseEntity.ok(ProtocolResponse(context, ResponseMessage.ack()))
        }
      )
  }

  fun onSearch(context: Context): ResponseEntity<ClientSearchResponse> {
    log.info("Got on search request for message id: {}", context.messageId)
    return messageService
      .findById(context.messageId)
      .flatMap { searchResponseStoreService.findByMessageId(context.messageId) }
      .flatMap { resEntity -> Right(resEntity.map { res -> catalogMapper.entityToSchema(res.message) }) }
      .fold(
        {
          log.error("Error when finding search response by message id. Error: {}", it)
          ResponseEntity
            .status(it.status().value())
            .body(ClientSearchResponse(context = context, error = it.error()))
        },
        {
          log.info("Found {} responses for message {}", it.size, context.messageId)
          ResponseEntity
            .ok(ClientSearchResponse(context = context, message = it))
        }
      )
  }
}
