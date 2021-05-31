package org.beckn.one.sandbox.bap

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class SandboxBapConfig {

  @Bean
  @Profile(value = ["!test"])
  fun database(
    @Value("\${database.mongo.url}") connectionString: String,
    @Value("\${database.mongo.dbname}") databaseName: String
  ): MongoDatabase {
    val client = KMongo.createClient(connectionString)
    return client.getDatabase(databaseName)
  }
}