package org.beckn.one.sandbox.bap.client.order.status.services

import arrow.core.Either
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderResponse
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class OnOrderStatusServiceSpec : DescribeSpec() {
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = Mockito.mock(UuidFactory::class.java)
  private val contextFactory = ContextFactoryInstance.create(uuidFactory, clock)

  init {
    describe("Validate On  Order Status Service") {

      it("should check if returns error") {

        val onOrderRepoFail = mock<ResponseStorageService<OrderResponse, OrderDao>> {
          onGeneric { updateDocByQuery(any(), any()) }.thenReturn(Either.Left(DatabaseError.OnWrite))
        }
        val onOrderStatusService = OnOrderStatusService(onOrderRepoFail)
        val orderDao = OrderDao(id = "23232323232",userId = "james")
        val response =  onOrderStatusService.updateOrder(orderDao)
        response.shouldBeLeft()
        response.value.shouldNotBeNull()
        response.value shouldBe DatabaseError.OnWrite
      }

      it("should check if returns success orderResponse") {
       val orderResponse = OrderResponse(
          userId = "rocky",
         context = null,
         messageId = "122221129272972",
         id = "122221129272972",
         error = null
        )
        val onOrderRepoSuccess = mock<ResponseStorageService<OrderResponse, OrderDao>> {
          onGeneric { updateDocByQuery(any(), any()) }
            .thenReturn(Either.Right(orderResponse))
        }
        val onOrderStatusService = OnOrderStatusService(onOrderRepoSuccess)
        val orderDao = OrderDao(id = "23232323232",userId = "james")
        val response =  onOrderStatusService.updateOrder(orderDao)
        response.shouldBeRight()
        response.value.shouldNotBeNull()
        response.value shouldBe orderResponse
      }
    }
  }
}