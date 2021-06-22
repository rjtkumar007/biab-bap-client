package org.beckn.one.sandbox.bap.message.repositories

import com.mongodb.client.MongoCollection
import org.beckn.one.sandbox.bap.message.entities.BecknResponse
import org.beckn.one.sandbox.bap.message.entities.Context
import org.litote.kmongo.div
import org.litote.kmongo.eq

class BecknResponseRepository<R : BecknResponse>(
  collection: MongoCollection<R>
) : GenericRepository<R>(collection) {

  fun findByMessageId(id: String): List<R> = findAll(BecknResponse::context / Context::messageId eq id)

}