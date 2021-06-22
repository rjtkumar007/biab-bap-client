package org.beckn.one.sandbox.bap.message.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.client.services.SearchService
import org.beckn.one.sandbox.bap.message.entities.Message
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class MessageService @Autowired constructor(
  @Qualifier("message-repo") private val messageRepository: GenericRepository<Message>
) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  fun save(message: Message): Either<HttpError, Message> = Either
    .catch { messageRepository.insertOne(message) }
    .mapLeft { e ->
      log.error("Error when saving message to DB", e)
      DatabaseError.OnWrite
    }
}
