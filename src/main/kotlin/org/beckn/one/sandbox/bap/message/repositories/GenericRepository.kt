package org.beckn.one.sandbox.bap.message.repositories

import arrow.core.computations.option
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.UpdateOptions
import org.beckn.one.sandbox.bap.Open
import org.beckn.one.sandbox.bap.message.entities.UserDao
import org.bson.Document
import org.bson.conversions.Bson
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.getCollectionOfName
import org.litote.kmongo.updateOneById
import org.litote.kmongo.util.idValue

@Open
class GenericRepository<R : Any>(private val collection: MongoCollection<R>) {

  companion object {
    inline fun <reified T : Any> create(database: MongoDatabase): GenericRepository<T> {
      return GenericRepository(database.getCollection())
    }

    inline fun <reified T : Any> create(database: MongoDatabase, collectionName: String): GenericRepository<T> {
      return GenericRepository(database.getCollectionOfName(collectionName))
    }
  }

  fun size(): Long = collection.countDocuments()

  fun insertMany(documents: List<R>) = collection.insertMany(documents)

  fun insertOne(document: R): R {
    collection.insertOne(document)
    return document;
  }

  fun findAll(query: Bson) = collection.find(query).toList()

  fun findOne(query: Bson) = collection.findOne(query)

  fun all() = collection.find().toList()

  fun clear() = collection.deleteMany(Document())

  fun update(id: String, entity: R, options: UpdateOptions) = collection.updateOneById(id,entity,options)

}
