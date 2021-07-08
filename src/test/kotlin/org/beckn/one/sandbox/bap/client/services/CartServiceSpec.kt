package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.dtos.CartResponseDtoV0
import org.beckn.one.sandbox.bap.client.dtos.CartResponseMessageDtoV0
import org.beckn.one.sandbox.bap.client.dtos.DeleteCartResponseDtoV0
import org.beckn.one.sandbox.bap.client.errors.validation.ValidationError
import org.beckn.one.sandbox.bap.client.factories.CartFactory
import org.beckn.one.sandbox.bap.client.mappers.CartMapperImpl
import org.beckn.one.sandbox.bap.client.repositories.CartRepository
import org.beckn.one.sandbox.bap.client.validators.CartValidator
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class CartServiceSpec : DescribeSpec() {
  private val contextFactory = ContextFactoryInstance.create()
  private val cartMapper = CartMapperImpl()
  private val cartValidator = CartValidator()

  init {
    describe("Save Cart") {
      val cartDto = CartFactory.create(id = "cart 1")
      val cartDao = cartMapper.dtoToDao(cartDto)
      val context = contextFactory.create()

      it("should return error when save cart fails") {
        val cartRepository = mock<CartRepository>()
        `when`(cartRepository.saveCart(cartDao)).thenReturn(Either.Left(DatabaseError.OnWrite))
        val cartService = createCartService(cartRepository)

        val response = cartService.saveCart(context, cartDto)

        verify(cartRepository).saveCart(cartDao)
        response shouldBeLeft DatabaseError.OnWrite
      }

      it("should return error when more than one BPP are present in the cart Object"){
        val cartRepository = mock<CartRepository>()
        val cartService = createCartService(cartRepository)
        val cartBppDto = CartFactory.createWithMultipleBpp(id = "cart 2")

        val response = cartService.saveCart(context, cartBppDto)

        response shouldBeLeft ValidationError.MultipleBpp
      }

      it("should return persisted dao when cart save succeeds") {
        val cartRepository = mock<CartRepository>()
        `when`(cartRepository.saveCart(cartDao)).thenReturn(Either.Right(cartDao))
        val cartService = createCartService(cartRepository)

        val response = cartService.saveCart(context, cartDto)

        verify(cartRepository).saveCart(cartDao)
        response shouldBeRight CartResponseDtoV0(
          context = context, message = CartResponseMessageDtoV0(cart = cartDto)
        )
      }
    }
    describe("Delete Cart"){
      val cartId =  "cart 1"
      val cartDto = CartFactory.create(id = cartId)
      val cartDao = cartMapper.dtoToDao(cartDto)
      val context = contextFactory.create()

      it("should return nothing when dao delete succeeds when dao is present") {
        val cartRepository = mock<CartRepository>()
        `when`(cartRepository.deleteById(cartId)).thenReturn(Either.Right(cartDao))
        val cartService = createCartService(cartRepository)

        val response = cartService.deleteCart(context, cartId)

        verify(cartRepository).deleteById(cartId)
        response shouldBeRight DeleteCartResponseDtoV0(context=context)
      }

      it("should return nothing when dao delete succeeds when dao is absent") {
        val cartRepository = mock<CartRepository>()
        `when`(cartRepository.deleteById(cartId)).thenReturn(Either.Left(DatabaseError.NotFound))
        val cartService = createCartService(cartRepository)

        val response = cartService.deleteCart(context, cartId)

        verify(cartRepository).deleteById(cartId)
        response shouldBeLeft DatabaseError.NotFound
      }
    }
  }

  private fun createCartService(cartRepository: CartRepository): CartService {
    return CartService(
      uuidFactory = UuidFactory(),
      cartMapper = cartMapper,
      cartValidator = cartValidator,
      cartRepository = cartRepository
    )
  }
}