package org.beckn.one.sandbox.bap.client.accounts.billings.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.shared.dtos.*
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.AddDeliveryAddressDao
import org.beckn.one.sandbox.bap.message.entities.BillingDao
import org.beckn.one.sandbox.bap.message.entities.BillingDetailsDao
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.litote.kmongo.newId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class BillingDetailService @Autowired constructor(
  private val billingRepository: GenericRepository<BillingDetailsDao>,
  private val responseStorageService: ResponseStorageService<BillingDetailsResponse, BillingDetailsDao>
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

//  fun save(billingDto: BillingDetailRequestDto): Either<HttpError, BillingDetailsDao> {
//    val user = SecurityUtil.getSecuredUserDetail()
//    val billingDao = BillingDao(
//
//    )
//
//    return Either
//      .catch { billingRepository.insertOne(BillingDetailsDao(userId = user?.uid ,billing = billingDao)) }
//      .mapLeft { e ->
//        log.error("Error when saving message to DB", e)
//        DatabaseError.OnWrite
//      }.map {
//        it
//      }
//
//  }

  fun findBillingsForCurrentUser(
    userId: String
  ): ResponseEntity<List<BillingDetailsResponse>> = responseStorageService
    .findByUserId(userId)
    .fold(
      {
        log.error("Error when finding search response by message id. Error: {}", it)
        ResponseEntity
          .status(it.status().value())
          .body(listOf(BillingDetailsResponse(userId = null,error = it.error(),context = null,billing = null)))
      },
      {
        log.info("Found responses for address {}", userId)
        ResponseEntity.ok(it)
      }
    )
}
