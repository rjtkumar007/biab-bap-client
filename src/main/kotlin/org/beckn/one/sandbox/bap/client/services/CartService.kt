package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.daos.CartDao
import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.dtos.CartResponseMessageDto
import org.beckn.one.sandbox.bap.client.dtos.CreateCartResponseDto
import org.beckn.one.sandbox.bap.client.dtos.DeleteCartResponseDto
import org.beckn.one.sandbox.bap.client.mappers.CartMapper
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.litote.kmongo.eq
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CartService @Autowired constructor(
  private val uuidFactory: UuidFactory,
  private val cartMapper: CartMapper,
  private val cartRepository: GenericRepository<CartDao>,
) {
  private val log: Logger = LoggerFactory.getLogger(CartService::class.java)

  fun saveCart(context: ProtocolContext, cartDto: CartDto): Either<DatabaseError, CreateCartResponseDto> {
    return Either.catch {
      log.info("Got request to save cart: {}", cartDto)
      val cartId = getOrGenerateCartId(cartDto)
      val cartDao = cartMapper.dtoToDao(cartDto.copy(id = cartId))
      log.info("Persisting cart: {}", cartDao)
      val upsertResult = cartRepository.upsert(cartDao, CartDao::id eq cartDao.id)
      log.info("Cart {} persist result: {}", cartId, upsertResult)
      return Either.Right(
        CreateCartResponseDto(
          context = context,
          message = CartResponseMessageDto(cart = cartDto.copy(id = cartId))
        )
      )
    }
      .mapLeft {
        log.error("Error when saving cart $cartDto", it)
        return Either.Left(DatabaseError.OnWrite)
      }
  }

  private fun getOrGenerateCartId(cartDto: CartDto) =
    cartDto.id ?: uuidFactory.create()

  fun deleteCart(context: ProtocolContext, cartId: String): Either<HttpError, DeleteCartResponseDto> {
    return Either.catch {
      return when (cartRepository.deleteOne(CartDao::id eq cartId)) {
        null -> Either.Left(DatabaseError.NotFound)
        else -> Either.Right(DeleteCartResponseDto(context = context))
      }
    }
      .mapLeft { e ->
        log.error("Error when deleting cart with id $cartId", e)
        DatabaseError.OnDelete
      }
  }
}
