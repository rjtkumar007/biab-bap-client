package org.beckn.one.sandbox.bap.client.accounts.user.services

import org.beckn.one.sandbox.bap.client.shared.dtos.AccountDetailsResponse
import org.beckn.one.sandbox.bap.message.entities.AccountDetailsDao
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.protocol.schemas.ProtocolError
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AccountDetailsServices @Autowired constructor(
  private val accountDetailRepository: GenericRepository<AccountDetailsDao>,
  private val responseStorageService: ResponseStorageService<AccountDetailsResponse, AccountDetailsDao>
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  /*fun save(address: UserDetails): Either<HttpError, AccountDetailsDao> {
    val user = SecurityUtil.getSecuredUserDetail()
    val deliveryAddressDao = DeliveryAddressDao(
      id =  newId<String>().toString(),
      descriptor = address.descriptor,
      gps = address.gps,
      default = address.default,
      address = address.address
    )

    return Either
      .catch { addressRepository.insertOne(AddDeliveryAddressDao(
        userId = user?.uid,
        address = deliveryAddressDao)) }
      .mapLeft { e ->
        log.error("Error when saving message to DB", e)
        DatabaseError.OnWrite
      }.map {
        it
      }

  }*/

  fun findAccountDetailForCurrentUser(
    userId: String
  ): ResponseEntity<AccountDetailsResponse> = responseStorageService
    .findByUserId(userId)
    .fold(
      {
        log.error("Error when finding search response by message id. Error: {}", it)
        ResponseEntity
          .status(it.status().value())
          .body(AccountDetailsResponse(userId = null,error = it.error(),context = null))
      },
      {
        log.info("Found responses for address {}", userId)
        ResponseEntity.ok(it)
      }
    )
}
