package org.beckn.one.sandbox.bap

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class SandboxBapTestConfig {
  @Bean
  fun database(): MongoDatabase {
    val host = MongoContainer.instance.host
    val port = MongoContainer.instance.getMappedPort(MongoContainer.MONGODB_PORT)
    val connectionString = "mongodb://$host:$port"
    val client = KMongo.createClient(connectionString)
    return client.getDatabase("bap_sandbox")
  }
}