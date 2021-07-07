package org.beckn.one.sandbox.bap.client.validators

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.daos.CartItemDao
import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.errors.validation.ValidationError
import org.beckn.one.sandbox.bap.errors.HttpError
import org.springframework.stereotype.Component

@Component
class CartValidator {
  fun validateCart(cart: CartDto): Either<HttpError, CartDto> {
    val emptyItems: List<CartItemDao> = emptyList()
    return when (cart.items) {
      emptyItems -> Either.Right(cart)
      null -> Either.Right(cart)
      else -> return if (cart.items.map { cartItemDto -> cartItemDto.bppId }.distinct().size > 1) Either.Left(
        ValidationError.MultipleBpp
      ) else Either.Right(cart)
    }
  }

}
