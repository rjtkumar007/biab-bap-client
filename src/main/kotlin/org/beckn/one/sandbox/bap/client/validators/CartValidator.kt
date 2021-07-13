package org.beckn.one.sandbox.bap.client.validators

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.daos.CartItemDao
import org.beckn.one.sandbox.bap.client.dtos.CartDtoV0
import org.beckn.one.sandbox.bap.client.errors.validation.CartError
import org.beckn.one.sandbox.bap.errors.HttpError
import org.springframework.stereotype.Component

@Component
class CartValidator {
  fun validateCart(cart: CartDtoV0): Either<HttpError, CartDtoV0> {
    val emptyItems: List<CartItemDao> = emptyList()
    return when (cart.items) {
      emptyItems -> Either.Right(cart)
      null -> Either.Right(cart)
      else -> return if (cart.items.map { cartItemDto -> cartItemDto.bppId }.distinct().size > 1) Either.Left(
        CartError.MultipleProviders
      ) else Either.Right(cart)
    }
  }

}
