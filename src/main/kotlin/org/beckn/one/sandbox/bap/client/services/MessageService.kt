package org.beckn.one.sandbox.bap.client.services

import org.beckn.one.sandbox.bap.common.entities.Message
import org.beckn.one.sandbox.bap.common.repositories.MessageRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class MessageService @Autowired constructor(
  private val messageRepository: MessageRepository
) {
  fun save(message: Message): Message {
    return messageRepository.save(message)
  }
}
