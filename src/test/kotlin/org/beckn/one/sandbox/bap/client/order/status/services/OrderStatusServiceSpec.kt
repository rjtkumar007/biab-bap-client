package org.beckn.one.sandbox.bap.client.order.status.services

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientContext
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderStatusDto
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.protocol.schemas.ProtocolOrderStatusRequestMessage
import org.mockito.Mockito.mock
import org.mockito.kotlin.verifyNoMoreInteractions

class OrderStatusServiceSpec : DescribeSpec() {
  private val context = ContextFactoryInstance.create().create()
  private val registryService = mock(RegistryService::class.java)
  private val bppOrderStatusService = mock(BppOrderStatusService::class.java)
  private val orderStatusService = OrderStatusService(
    registryService = registryService,
    bppOrderStatusService = bppOrderStatusService,
  )

  init {
    describe("Get Order Status") {
      it("should validate that bpp id is not null") {
        val getOrderStatusResponse = orderStatusService.getOrderStatus(
          context = context,
          request = OrderStatusDto(
            context = ClientContext(transactionId = UuidFactory().create(), bppId = null),
            message = ProtocolOrderStatusRequestMessage(orderId = "Order Id 1")
          )
        )

        getOrderStatusResponse shouldBeLeft BppError.BppIdNotPresent
        verifyNoMoreInteractions(registryService)
        verifyNoMoreInteractions(bppOrderStatusService)
      }
    }
  }
}