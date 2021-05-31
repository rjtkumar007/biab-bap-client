package org.beckn.one.sandbox.bap

import com.mongodb.client.MongoDatabase
import org.beckn.one.sandbox.bap.repositories.entities.Category
import org.beckn.one.sandbox.bap.repositories.GenericCollection
import org.beckn.one.sandbox.bap.repositories.entities.Item
import org.litote.kmongo.KMongo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class SandboxBapConfig {

  @Bean
  @Profile(value = ["!test"])
  fun database(@Value("\${database.mongo.url}") connectionString: String,
               @Value("\${database.mongo.dbname}") databaseName: String): MongoDatabase {
    val client = KMongo.createClient(connectionString)
    return client.getDatabase(databaseName)
  }

  @Bean
  fun categoryCollection(@Autowired database: MongoDatabase): GenericCollection<Category> =
    GenericCollection.create(database)

  @Bean
  fun itemCollection(@Autowired database: MongoDatabase): GenericCollection<Item> =
    GenericCollection.create(database)
}