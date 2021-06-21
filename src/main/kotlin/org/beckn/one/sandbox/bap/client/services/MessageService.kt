package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.errors.HttpError
import org.beckn.one.sandbox.bap.common.entities.Message
import org.beckn.one.sandbox.bap.common.repositories.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MessageService @Autowired constructor(
  private val messageRepository: MessageRepository
) {
  fun save(message: Message): Either<HttpError, Message> {
    return Either.Right(messageRepository.save(message))
  }
}
