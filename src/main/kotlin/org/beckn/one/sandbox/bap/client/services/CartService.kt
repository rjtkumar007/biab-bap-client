package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.mappers.SelectedItemMapper
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.message.entities.MessageDao
import org.beckn.one.sandbox.bap.message.services.MessageService
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolLocation
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CartService @Autowired constructor(
  private val messageService: MessageService,
  private val bppService: BppService,
  private val selectedItemMapper: SelectedItemMapper,
  private val log: Logger = LoggerFactory.getLogger(CartService::class.java)
) {
  fun saveCart(context: ProtocolContext, cart: CartDto): Either<HttpError, MessageDao> {
    log.info("Got save cart request. Context: {}, Cart: {}", context, cart)
    cart.items?.first()?.bppUri?.let { bppUri ->
      bppService.select(
        context,
        bppUri = bppUri,
        providerId = cart.items.first().provider.id,
        providerLocation = ProtocolLocation(gps = cart.items.first().provider.locations?.first()),
        items = cart.items.map { selectedItemMapper.dtoToProtocol(it) }
      )
    }
    return messageService.save(MessageDao(id = context.messageId, type = MessageDao.Type.Select))
  }
}
