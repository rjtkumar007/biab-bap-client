package org.beckn.one.sandbox.bap.client.accounts.address.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.auth.utils.SecurityUtil
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressRequestDto
import org.beckn.one.sandbox.bap.client.shared.dtos.DeliveryAddressResponse
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.AddDeliveryAddressDao
import org.beckn.one.sandbox.bap.message.mappers.GenericResponseMapper
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.litote.kmongo.eq
import org.litote.kmongo.newId
import org.litote.kmongo.setTo
import org.litote.kmongo.setValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class AddressServices @Autowired constructor(
  private val addressRepository: BecknResponseRepository<AddDeliveryAddressDao>,
  private val responseStorageService: ResponseStorageService<DeliveryAddressResponse, AddDeliveryAddressDao>,
  private val mapper: GenericResponseMapper<DeliveryAddressResponse, AddDeliveryAddressDao>

) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun updateAndSaveAddress(address: DeliveryAddressRequestDto): Either<HttpError, DeliveryAddressResponse> {
    val user = SecurityUtil.getSecuredUserDetail()
    val addressDao= AddDeliveryAddressDao(
      userId = user?.uid,
      id =  newId<String>().toString(),
      descriptor = address.descriptor,
      gps = address.gps,
      defaultAddress = true,
      address = address.address)

    return Either
      .catch {
        addressRepository.updateManyById(
          AddDeliveryAddressDao::userId eq user?.uid,
          setValue(AddDeliveryAddressDao::defaultAddress, false))
      }
      .mapLeft { e ->
        log.error("Error when saving message to DB", e)
        DatabaseError.OnWrite
      }
      .map {
        return save(addressDao)
      }
  }

  fun save(requestDao: AddDeliveryAddressDao): Either<DatabaseError.OnWrite, DeliveryAddressResponse> = Either
      .catch {
        log.info("Saving client response: {}", requestDao)
        addressRepository.insertOne(requestDao)
      }
      .bimap(
        rightOperation = {
          mapper.entityToProtocol(it)
        },
        leftOperation = {
          log.error("Exception while saving search response", it)
          DatabaseError.OnWrite
        }
      )
  
  fun findAddressesForCurrentUser(
    userId: String
  ): ResponseEntity<List<DeliveryAddressResponse>> = responseStorageService
    .findManyByUserId(userId,0,0)
    .fold(
      {
        log.error("Error when finding search response by message id. Error: {}", it)
        mapToErrorResponse(it)
      },
      {
        log.info("Found responses for address {}", userId)
        ResponseEntity.ok(it)
      }
    )

  private fun mapToErrorResponse(it: HttpError) = ResponseEntity
    .status(it.status())
    .body(
      listOf(DeliveryAddressResponse(
        id = null,
        userId = null,
        context = null,
        error = it.error()
      )
    ))
}
