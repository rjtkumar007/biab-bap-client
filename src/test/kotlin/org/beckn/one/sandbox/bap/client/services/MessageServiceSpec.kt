package org.beckn.one.sandbox.bap.client.services

import com.mongodb.MongoException
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.MessageDao
import org.beckn.one.sandbox.bap.message.repositories.GenericRepository
import org.beckn.one.sandbox.bap.message.services.MessageService
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.junit.jupiter.api.Assertions.fail
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify


class MessageServiceSpec : DescribeSpec() {

  init {
    it("should return error when unable to save message") {
      val message = MessageDao(id = UuidFactory().create(), type = MessageDao.Type.Search)
      val repository = mock<GenericRepository<MessageDao>>{
        onGeneric { insertOne(message) }.thenThrow(MongoException("Write error"))
      }
      val service = MessageService(repository)

     // `when`(repository.insertOne(message)).thenThrow(MongoException("Write error"))
      val saveResult = service.save(message)

      saveResult.fold(
        {
          it shouldBe DatabaseError.OnWrite
        },
        {
          fail("Expected test to fail with DatabaseError.OnWrite but it did not")
        }
      )
    }

    it("should invoke repository to save message") {
      val message = MessageDao(id = UuidFactory().create(), type = MessageDao.Type.Search)
      val repository = mock<GenericRepository<MessageDao>>{
        onGeneric{ insertOne(message) }.thenReturn(message)
    }
      val service = MessageService(repository)
      //`when`(repository.insertOne(message)).thenReturn(message)

      val saveResult = service.save(message)

      saveResult.fold(
        {
          fail("Saved failed. Error: $it")
        },
        {
          it shouldBe message
          verify(repository).insertOne(message)
        }
      )
    }
  }
}