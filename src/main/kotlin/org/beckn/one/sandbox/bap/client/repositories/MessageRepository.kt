package org.beckn.one.sandbox.bap.client.repositories

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.beckn.one.sandbox.bap.client.entities.Message
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.save
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class MessageRepository @Autowired constructor(
  private val db: MongoDatabase
) {
  fun findById(id: String): Message? {
    return collection().findOne(Message::id eq id)
  }

  fun save(message: Message): Message {
    collection().save(message)
    return message
  }

  private fun collection(): MongoCollection<Message> {
    return db.getCollection("messages", Message::class.java)
  }
}
