package org.beckn.one.sandbox.bap.configurations

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoDatabase
import org.beckn.one.sandbox.bap.client.shared.dtos.BillingDetailsResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressResponse
import org.beckn.one.sandbox.bap.message.entities.*
import org.beckn.one.sandbox.bap.message.mappers.GenericResponseMapper
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.message.services.ResponseStorageServiceImpl
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
  fun setUserRepo(@Autowired database: MongoDatabase): GenericRepository<UserDao> =
    GenericRepository.create(database, "user")

  @Bean
  fun setOrderRepo(@Autowired database: MongoDatabase): GenericRepository<OrderDao> =
    GenericRepository.create(database, "order")

  @Bean
  fun onDeliverAddressResponseRepo(@Autowired database: MongoDatabase): BecknResponseRepository<AddDeliveryAddressDao> =
    BecknResponseRepository(database.getCollectionOfName("delivery_address"))


  @Bean
  fun setBillingResponseRepo(@Autowired database: MongoDatabase): BecknResponseRepository<BillingDetailsDao> =
    BecknResponseRepository(database.getCollectionOfName("billing"))


  @Bean
  fun addDeliveryAddress(
    @Autowired responseRepository: BecknResponseRepository<AddDeliveryAddressDao>,
    @Autowired mapper: GenericResponseMapper<DeliveryAddressResponse, AddDeliveryAddressDao>,
  ): ResponseStorageService<DeliveryAddressResponse,AddDeliveryAddressDao> = ResponseStorageServiceImpl(responseRepository,mapper)

  @Bean
  fun setBillingDetails(
    @Autowired responseRepository: BecknResponseRepository<BillingDetailsDao>,
    @Autowired mapper: GenericResponseMapper<BillingDetailsResponse, BillingDetailsDao>,
  ): ResponseStorageService<BillingDetailsResponse,BillingDetailsDao> = ResponseStorageServiceImpl(responseRepository,mapper)

}
