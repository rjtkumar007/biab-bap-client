package org.beckn.one.sandbox.bap.client.services

import com.mongodb.MongoException
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.daos.CartDao
import org.beckn.one.sandbox.bap.client.factories.CartFactory
import org.beckn.one.sandbox.bap.client.factories.DbIdFactory
import org.beckn.one.sandbox.bap.client.mappers.CartMapperImpl
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.junit.jupiter.api.Assertions
import org.junit.runner.RunWith
import org.litote.kmongo.eq
import org.litote.kmongo.id.StringId
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
internal class CartServiceSpec : DescribeSpec() {
  private val contextFactory = ContextFactoryInstance.create()
  private val cartMapper = CartMapperImpl()

  @field:Mock
  private lateinit var cartRepository: GenericRepository<CartDao>

  init {
    describe("Save Cart") {
      val cartDto = CartFactory.create(id = "cart 1")
      val cartDao = cartMapper.dtoToDao(cartDto).copy(_id = StringId("cart bson 1"))
      val dbIdFactory = mock<DbIdFactory>()
      DbIdFactory.setInstance(dbIdFactory)

      it("should return error when db write fails") {
        val cartRepository = mock<GenericRepository<CartDao>> {
          onGeneric { upsert(any(), any()) }.thenThrow(MongoException("Write error"))
        }
        `when`(dbIdFactory.createStringId()).thenReturn(cartDao._id)
        val cartService = createCartService(cartRepository)

        val response = cartService.saveCart(contextFactory.create(), cartDto)

        verify(cartRepository).upsert(cartDao, CartDao::id eq cartDao.id)
        response
          .fold(
            { it shouldBe DatabaseError.OnWrite },
            { Assertions.fail("Save cart should have failed with DatabaseError.OnWrite but didn't. Response: $it") }
          )
      }
    }
  }

  private fun createCartService(cartRepository: GenericRepository<CartDao>): CartService {
    return CartService(
      uuidFactory = UuidFactory(),
      cartMapper = cartMapper,
      cartRepository = cartRepository
    )
  }
}