package org.beckn.one.sandbox.bap.client.order.status.services

import arrow.core.Either
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.external.provider.BppClient
import org.beckn.one.sandbox.bap.client.external.provider.BppClientFactory
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.protocol.schemas.*
import org.mockito.Mockito.*
import retrofit2.mock.Calls
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class BppOrderStatusServiceSpec : DescribeSpec() {
  private val bppServiceClientFactory = mock(BppClientFactory::class.java)
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = mock(UuidFactory::class.java)
  private val contextFactory = ContextFactoryInstance.create(uuidFactory, clock)
  private val bppOrderStatusService = BppOrderStatusService(bppServiceClientFactory)
  private val bppServiceClient: BppClient = mock(BppClient::class.java)
  private val bppUri = "https://bpp1.com"

  init {
    describe("BPP Order Status") {
      `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      `when`(bppServiceClientFactory.getClient(bppUri)).thenReturn(bppServiceClient)
      val getOrderStatusRequest = getProtocolOrderStatusRequest()

      beforeEach {
        reset(bppServiceClient)
      }

      it("should return bpp internal server error when bpp get order status call fails with an exception") {
        `when`(bppServiceClient.getOrderStatus(getProtocolOrderStatusRequest())).thenReturn(
          Calls.failure(IOException("Timeout"))
        )

        val response = invokeBppGetOrderStatus()

        response shouldBeLeft BppError.Internal
        verify(bppServiceClient).getOrderStatus(getProtocolOrderStatusRequest())
      }

      it("should return bpp internal server error when bpp get order status call returns null body") {
        `when`(bppServiceClient.getOrderStatus(getOrderStatusRequest)).thenReturn(
          Calls.response(null)
        )

        val response = invokeBppGetOrderStatus()

        response shouldBeLeft BppError.NullResponse
        verify(bppServiceClient).getOrderStatus(getProtocolOrderStatusRequest())
      }

      it("should return bpp internal server error when bpp get order status call returns nack response body") {
        val context = contextFactory.create(action = ProtocolContext.Action.STATUS)
        `when`(bppServiceClient.getOrderStatus(getOrderStatusRequest)).thenReturn(
          Calls.response(ProtocolAckResponse(context, ResponseMessage.nack()))
        )

        val response = invokeBppGetOrderStatus()

        response shouldBeLeft BppError.Nack
        verify(bppServiceClient).getOrderStatus(getProtocolOrderStatusRequest())
      }
    }
  }

  private fun invokeBppGetOrderStatus(): Either<BppError, ProtocolAckResponse> {
    return bppOrderStatusService.getOrderStatus(
      bppUri,
      contextFactory.create(action = ProtocolContext.Action.STATUS),
      getProtocolOrderStatusRequest().message
    )
  }

  private fun getProtocolOrderStatusRequest() = ProtocolOrderStatusRequest(
    context = contextFactory.create(action = ProtocolContext.Action.STATUS),
    message = ProtocolOrderStatusRequestMessage(
      orderId = "order id 1"
    )
  )
}
