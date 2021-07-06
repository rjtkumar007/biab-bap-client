package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.dtos.CartResponseMessageDto
import org.beckn.one.sandbox.bap.client.dtos.CreateCartResponseDto
import org.beckn.one.sandbox.bap.client.factories.CartFactory
import org.beckn.one.sandbox.bap.client.factories.DbIdFactory
import org.beckn.one.sandbox.bap.client.mappers.CartMapperImpl
import org.beckn.one.sandbox.bap.client.repositories.CartRepository
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.junit.runner.RunWith
import org.litote.kmongo.id.StringId
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class CartServiceSpec : DescribeSpec() {
  private val contextFactory = ContextFactoryInstance.create()
  private val cartMapper = CartMapperImpl()

  init {
    describe("Save Cart") {
      val cartDto = CartFactory.create(id = "cart 1")
      val cartDao = cartMapper.dtoToDao(cartDto).copy(_id = StringId("cart bson 1"))
      val dbIdFactory = mock<DbIdFactory>()
      val context = contextFactory.create()
      DbIdFactory.setInstance(dbIdFactory)
      `when`(dbIdFactory.createStringId()).thenReturn(cartDao._id)

      it("should return error when save cart fails") {
        val cartRepository = mock<CartRepository>()
        `when`(cartRepository.saveCart(cartDao)).thenReturn(Either.Left(DatabaseError.OnWrite))
        val cartService = createCartService(cartRepository)

        val response = cartService.saveCart(context, cartDto)

        verify(cartRepository).saveCart(cartDao)
        response shouldBeLeft DatabaseError.OnWrite
      }

      it("should return persisted dao when cart save succeeds") {
        val cartRepository = mock<CartRepository>()
        `when`(cartRepository.saveCart(cartDao)).thenReturn(Either.Right(cartDao))
        val cartService = createCartService(cartRepository)

        val response = cartService.saveCart(context, cartDto)

        verify(cartRepository).saveCart(cartDao)
        response shouldBeRight CreateCartResponseDto(
          context = context, message = CartResponseMessageDto(cart = cartDto)
        )
      }
    }
  }

  private fun createCartService(cartRepository: CartRepository): CartService {
    return CartService(
      uuidFactory = UuidFactory(),
      cartMapper = cartMapper,
      cartRepository = cartRepository
    )
  }
}