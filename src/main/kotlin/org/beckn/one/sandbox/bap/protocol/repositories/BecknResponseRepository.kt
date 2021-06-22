package org.beckn.one.sandbox.bap.protocol.repositories

import org.beckn.one.sandbox.bap.protocol.entities.BecknResponse
import org.beckn.one.sandbox.bap.protocol.entities.Context
import org.litote.kmongo.div
import org.litote.kmongo.eq

fun <R: BecknResponse> GenericRepository<R>.findByMessageId(id: String): List<R> = findAll(BecknResponse::context / Context::messageId eq id)

/*class BecknResponseRepository<R: BecknResponse>(val repo: GenericRepository<R>) {

  fun findByMessageId(id: String): List<R> = repo.findAll(BecknResponse :: context / Context::messageId eq id)

  fun save(response: R) = repo.insertOne(response)

  // other methods
}*/