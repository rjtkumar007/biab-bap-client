package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.dtos.SearchCriteria
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.message.entities.MessageDao
import org.beckn.one.sandbox.bap.message.services.MessageService
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SearchService(
  @Autowired val registryService: RegistryService,
  @Autowired val gatewayService: GatewayService,
  @Autowired val messageService: MessageService
) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun search(context: ProtocolContext, criteria: SearchCriteria): Either<HttpError, MessageDao> {
    log.info("Got search request with criteria: {} ", criteria)
    return when (criteria.providerId) {
      null -> registryService
        .lookupGateways()
        .flatMap { gatewayService.search(it.first(), context, criteria) }
        .flatMap { messageService.save(MessageDao(id = context.messageId, type = MessageDao.Type.Search)) }
      else -> registryService
        .lookupGateways()
        .flatMap { gatewayService.searchProvider(it.first(), context, criteria) }
        .flatMap { messageService.save(MessageDao(id = context.messageId, type = MessageDao.Type.Search)) }
    }
  }
}
