package org.beckn.one.sandbox.bap.configurations

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoDatabase
import org.beckn.one.sandbox.bap.message.entities.Message
import org.beckn.one.sandbox.bap.message.entities.SearchResponse
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollectionOfName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfiguration @Autowired constructor(
  @Value("\${database.mongo.url}") private val connectionString: String,
  @Value("\${database.mongo.name}") private val databaseName: String
) {
  @Bean
  fun database(): MongoDatabase {
    val settings = MongoClientSettings.builder()
      .applyConnectionString(ConnectionString(connectionString))
      .build()
    val client = KMongo.createClient(settings)
    return client.getDatabase(databaseName)
  }

  @Bean
  fun searchResponseRepo(@Autowired database: MongoDatabase): BecknResponseRepository<SearchResponse> =
    BecknResponseRepository(database.getCollectionOfName("search_responses"))

  @Bean
  fun messageResponseRepo(@Autowired database: MongoDatabase): GenericRepository<Message> =
    GenericRepository.create(database, "message_responses")
}