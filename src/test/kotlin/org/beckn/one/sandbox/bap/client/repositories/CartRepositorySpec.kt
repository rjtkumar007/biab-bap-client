package org.beckn.one.sandbox.bap.client.repositories

import com.mongodb.MongoException
import com.mongodb.client.result.UpdateResult
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.daos.CartDao
import org.beckn.one.sandbox.bap.client.factories.CartFactory
import org.beckn.one.sandbox.bap.client.mappers.CartMapperImpl
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.junit.runner.RunWith
import org.litote.kmongo.eq
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class CartRepositorySpec : DescribeSpec() {

  init {
    describe("Save Cart") {
      val cartMapper = CartMapperImpl()
      val cartDto = CartFactory.create(id = "cart 1")
      val cartDao = cartMapper.dtoToDao(cartDto)

      it("should return error when cart save fails with db write error") {
        val genericRepository = mock<GenericRepository<CartDao>> {
          onGeneric { upsert(any(), any()) }.thenThrow(MongoException("Write error"))
        }
        val cartRepository = CartRepository(genericRepository)

        val response = cartRepository.saveCart(cartDao)

        verify(genericRepository).upsert(cartDao, CartDao::id eq cartDao.id)
        response shouldBeLeft DatabaseError.OnWrite
      }

      it("should return persisted dao when cart save succeeds") {
        val genericRepository = mock<GenericRepository<CartDao>> {
          onGeneric { upsert(any(), any()) }.thenReturn(UpdateResult.acknowledged(1, 1, null))
        }
        val cartRepository = CartRepository(genericRepository)

        val response = cartRepository.saveCart(cartDao)

        verify(genericRepository).upsert(cartDao, CartDao::id eq cartDao.id)
        response shouldBeRight cartDao
      }
    }
  }
}