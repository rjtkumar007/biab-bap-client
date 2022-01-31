package org.beckn.one.sandbox.bap.message.repositories

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import org.beckn.one.sandbox.bap.message.entities.BecknResponseDao
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.bson.conversions.Bson
import org.litote.kmongo.eq
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BecknResponseRepository<R : BecknResponseDao>(
  val collection: MongoCollection<R>
) : GenericRepository<R>(collection) {
  private val log: Logger = LoggerFactory.getLogger(this::class.java)


  fun findManyByUserId(id: String, skip: Int = 0 , limit :Int = 10): List<R> =
    collection.find(BecknResponseDao::userId eq id).limit(limit).skip(skip).toList()

  fun findById(id: String): R? {
    return findOne(BecknResponseDao::userId eq id)
  }

  fun updateByIdQuery(id: Bson, requestData: R, updateOptions: UpdateOptions): UpdateResult {
    return updateOneByQuery(
      id,
      requestData,
      updateOptions
    )
  }
  fun findOrdersById(bson: Bson, skip: Int = 0 , limit :Int = 10  ): List<R> =
    collection.find(bson).limit(limit).skip(skip).toList()

  fun updateManyById(id: Bson, requestColumn:Bson): UpdateResult {
    return updateManyColumnById(
      id,
      requestColumn
    )
  }
}