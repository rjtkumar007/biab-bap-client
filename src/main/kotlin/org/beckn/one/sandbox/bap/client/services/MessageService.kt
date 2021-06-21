package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.errors.HttpError
import org.beckn.one.sandbox.bap.client.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.common.entities.Message
import org.beckn.one.sandbox.bap.common.repositories.MessageRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MessageService @Autowired constructor(
  private val messageRepository: MessageRepository
) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun save(message: Message): Either<HttpError, Message> =
    try {
      val savedMessage = messageRepository.save(message)
      log.info("Successfully saved message: {}", message)
      Either.Right(savedMessage)
    } catch (e: Exception) {
      log.error("Error when saving message to DB", e)
      Either.Left(DatabaseError.OnWrite)
    }
}
