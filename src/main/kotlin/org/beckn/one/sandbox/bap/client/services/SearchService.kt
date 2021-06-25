package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.message.entities.Message
import org.beckn.one.sandbox.bap.message.services.MessageService
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.schemas.ProtocolCatalog
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SearchService(
  @Autowired val registryService: RegistryService,
  @Autowired val gatewayService: GatewayService,
  @Autowired val messageService: MessageService,
  @Autowired val searchResponseStorageService: ResponseStorageService<ProtocolSearchResponse>
) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun search(context: ProtocolContext, queryString: String?): Either<HttpError, Message> {
    log.info("Got search request: {}", queryString)
    return registryService
      .lookupGateways()
      .flatMap { gatewayService.search(it.first(), queryString) }
      .flatMap { messageService.save(Message(id = context.messageId, type = Message.Type.Search)) }
  }

  fun onSearch(messageId: String): Either<HttpError, List<ProtocolCatalog>> {
    log.info("Got on search request for message id: {}", messageId)
    return messageService
      .findById(messageId)
      .flatMap { searchResponseStorageService.findByMessageId(messageId) }
      .map { it.mapNotNull { response -> response.message?.catalog } }

  }
}
