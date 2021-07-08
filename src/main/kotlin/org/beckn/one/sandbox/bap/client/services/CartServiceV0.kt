package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.dtos.CartDtoV0
import org.beckn.one.sandbox.bap.client.dtos.CartResponseDtoV0
import org.beckn.one.sandbox.bap.client.dtos.CartResponseMessageDtoV0
import org.beckn.one.sandbox.bap.client.dtos.DeleteCartResponseDtoV0
import org.beckn.one.sandbox.bap.client.mappers.CartMapper
import org.beckn.one.sandbox.bap.client.repositories.CartRepository
import org.beckn.one.sandbox.bap.client.validators.CartValidator
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CartServiceV0 @Autowired constructor(
  private val uuidFactory: UuidFactory,
  private val cartMapper: CartMapper,
  private val cartRepository: CartRepository,
  private val cartValidator: CartValidator,
  private val log: Logger = LoggerFactory.getLogger(CartServiceV0::class.java)
) {
  fun saveCart(context: ProtocolContext, cartDto: CartDtoV0): Either<HttpError, CartResponseDtoV0> {
    log.info("Got request to save cart: {}", cartDto)
    val cartDtoWithId = getCartWithNewIdIfNotPresent(cartDto)
    val cartToPersist = cartMapper.dtoToDao(cartDtoWithId)
    return cartValidator.validateCart(cartDtoWithId).flatMap { cartRepository.saveCart(cartToPersist) }
      .fold({ Left(it) },
        {
          Right(
            CartResponseDtoV0(context = context, message = CartResponseMessageDtoV0(cart = cartMapper.daoToDto(it)))
          )
        })
  }

  private fun getCartWithNewIdIfNotPresent(cartDto: CartDtoV0): CartDtoV0 =
    cartDto.copy(id = cartDto.id ?: uuidFactory.create())

  fun deleteCart(context: ProtocolContext, id: String): Either<HttpError, DeleteCartResponseDtoV0> =
    cartRepository.deleteById(id).fold(
      { Left(it) },
      { Right(DeleteCartResponseDtoV0(context = context)) }
    )

}
