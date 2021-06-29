package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.dtos.ClientSearchResponse
import org.beckn.one.sandbox.bap.client.dtos.ClientSearchResponseMessage
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.message.entities.Message
import org.beckn.one.sandbox.bap.message.services.MessageService
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSearch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SearchService(
  @Autowired val registryService: RegistryService,
  @Autowired val gatewayService: GatewayService,
  @Autowired val messageService: MessageService,
  @Autowired val responseStoreService: ResponseStorageService<ProtocolOnSearch>
) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun search(context: ProtocolContext, queryString: String?): Either<HttpError, Message> {
    log.info("Got search request: {}", queryString)
    return registryService
      .lookupGateways()
      .flatMap { gatewayService.search(it.first(), queryString) }
      .flatMap { messageService.save(Message(id = context.messageId, type = Message.Type.Search)) }
  }

  fun onSearch(context: ProtocolContext): Either<HttpError, ClientSearchResponse> {
    log.info("Got on search request for message id: {}", context.messageId)
    return messageService
      .findById(context.messageId)
      .flatMap { responseStoreService.findByMessageId(context.messageId) }
      .map { it.mapNotNull { response -> response.message?.catalog } }
      .map {
        ClientSearchResponse(
          context = context,
          message = ClientSearchResponseMessage(it)
        )
      }
  }
}
