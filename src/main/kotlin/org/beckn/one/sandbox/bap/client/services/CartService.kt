package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.dtos.CartResponseMessageDto
import org.beckn.one.sandbox.bap.client.dtos.CartResponseDto
import org.beckn.one.sandbox.bap.client.dtos.DeleteCartResponseDto
import org.beckn.one.sandbox.bap.client.mappers.CartMapper
import org.beckn.one.sandbox.bap.client.repositories.CartRepository
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CartService @Autowired constructor(
  private val uuidFactory: UuidFactory,
  private val cartMapper: CartMapper,
  private val cartRepository: CartRepository,
  private val log: Logger = LoggerFactory.getLogger(CartService::class.java)
) {
  fun saveCart(context: ProtocolContext, cartDto: CartDto): Either<HttpError, CartResponseDto> {
    log.info("Got request to save cart: {}", cartDto)
    val cartDtoWithId = getCartWithNewIdIfNotPresent(cartDto)
    val cartToPersist = cartMapper.dtoToDao(cartDtoWithId)
    return cartRepository.saveCart(cartToPersist)
      .fold({ Left(it) },
        {
          Right(
            CartResponseDto(context = context, message = CartResponseMessageDto(cart = cartMapper.daoToDto(it)))
          )
        })
  }

  private fun getCartWithNewIdIfNotPresent(cartDto: CartDto): CartDto =
    cartDto.copy(id = cartDto.id ?: uuidFactory.create())

  fun deleteCart(context: ProtocolContext, id: String): Either<HttpError, DeleteCartResponseDto> =
    Either.catch {
      return when (cartRepository.deleteById(id)) {
        null -> Left(DatabaseError.NotFound)
        else -> Right(DeleteCartResponseDto(context = context))
      }
    }
      .mapLeft { e ->
        log.error("Error when deleting cart with id $id", e)
        DatabaseError.OnDelete
      }
}
