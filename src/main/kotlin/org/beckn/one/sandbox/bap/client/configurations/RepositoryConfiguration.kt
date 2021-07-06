package org.beckn.one.sandbox.bap.client.configurations

import com.mongodb.client.MongoDatabase
import org.beckn.one.sandbox.bap.client.daos.CartDao
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.litote.kmongo.getCollectionOfName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RepositoryConfiguration {
  @Bean
  fun cartGenericRepository(@Autowired database: MongoDatabase): GenericRepository<CartDao> {
    return GenericRepository(database.getCollectionOfName(CartDao.collectionName))
  }
}