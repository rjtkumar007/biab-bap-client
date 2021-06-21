package org.beckn.one.sandbox.bap.client.services

import com.mongodb.MongoException
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.client.entities.Message
import org.beckn.one.sandbox.bap.common.factories.UuidFactory
import org.beckn.one.sandbox.bap.client.repositories.MessageRepository
import org.junit.jupiter.api.Assertions.fail
import org.mockito.Mockito.*

internal class MessageServiceSpec : DescribeSpec() {
  init {
    it("should return error when unable to save message") {
      val repository = mock(MessageRepository::class.java)
      val service = MessageService(repository)
      val message = Message(id = UuidFactory().create(), type = Message.Type.Search)
      `when`(repository.save(message)).thenThrow(MongoException("Write error"))

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
      val repository = mock(MessageRepository::class.java)
      val service = MessageService(repository)
      val message = Message(id = UuidFactory().create(), type = Message.Type.Search)
      `when`(repository.save(message)).thenReturn(message)

      val saveResult = service.save(message)

      saveResult.fold(
        {
          fail("Saved failed. Error: $it")
        },
        {
          it shouldBe message
          verify(repository).save(message)
        }
      )
    }
  }
}