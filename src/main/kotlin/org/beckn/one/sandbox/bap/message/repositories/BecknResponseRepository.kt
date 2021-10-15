package org.beckn.one.sandbox.bap.message.repositories

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.UpdateResult
import org.beckn.one.sandbox.bap.message.entities.*
import org.litote.kmongo.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class BecknResponseRepository<R : BecknResponseDao>(
  val collection: MongoCollection<R>
) : GenericRepository<R>(collection) {
  private val log: Logger = LoggerFactory.getLogger(this::class.java)


  fun findManyByUserId(id: String): List<R> = findAll(BecknResponseDao::userId eq id)

  fun findByUserId(id: String): R? = findOne(BecknResponseDao::userId eq id)

  fun updateOneEntityById(requestData: R, updateOptions: UpdateOptions): UpdateResult = update(
    requestData.userId!!,
    requestData, updateOptions
  )

  fun findDataFromAllCollections(userId: String){
    val look = lookup(
      from = "billing",
      localField = "userId",
      foreignField = "userId",
      newAs = "billing_info"
    )
   val iterator =  collection.aggregate(
      listOf(
        look
      )
    )
  }



  /*collection.aggregate<AccountDetailsDao>(
  match(
    BillingDetailsDao::userId eq  userId
  ),
  group(
    AccountDetailsDao::billingInfo.push(BillingDetailsDao::billing)
  )
)
*/


}