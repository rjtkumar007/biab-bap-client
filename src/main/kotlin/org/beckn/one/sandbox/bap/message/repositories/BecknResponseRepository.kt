package org.beckn.one.sandbox.bap.message.repositories

import com.mongodb.client.MongoCollection
import org.beckn.one.sandbox.bap.message.entities.*
import org.litote.kmongo.eq

class BecknResponseRepository<R : BecknResponseDao>(
  collection: MongoCollection<R>
) : GenericRepository<R>(collection) {


  fun findByUserId(id: String): List<R> = findAll(BecknResponseDao::userId eq id)


}