package org.beckn.one.sandbox.bap.client.order.status.services

import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.external.provider.BppClient
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderResponse
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.mockito.Mockito
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class OnOrderStatusServiceSpec @Autowired constructor(
  val repository: ResponseStorageService<OrderResponse, OrderDao>
) : DescribeSpec() {
  init {
    describe("Get Order Status") {
      val onOrderStatusService = OnOrderStatusService(repository)
      val orderDao = OrderDao(messageId = "23232323232",userId = "james")
      it("should check if returns order response") {
        val response =  onOrderStatusService.updateOrder(orderDao)
        response.shouldBeRight()
        response.value.shouldNotBeNull()
      }
    }
  }
}