package org.beckn.one.sandbox.bap.client.order.cancel.services

import arrow.core.Either
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.external.provider.BppClient
import org.beckn.one.sandbox.bap.client.external.provider.BppClientFactory
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientContext
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.protocol.schemas.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import retrofit2.mock.Calls
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class BppCancelServiceSpec : DescribeSpec() {
  private val bppServiceClientFactory = mock(BppClientFactory::class.java)
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = mock(UuidFactory::class.java)
  private val contextFactory = ContextFactoryInstance.create(uuidFactory, clock)
  private val bppCancelService = BppCancelService(bppServiceClientFactory)
  private val bppServiceClient: BppClient = mock(BppClient::class.java)
  private val bppUri = "https://bpp1.com"

  init {
    describe("Cancel") {
      `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      `when`(bppServiceClientFactory.getClient(bppUri)).thenReturn(bppServiceClient)
      val cancelRequest = getCancelRequest()

      beforeEach {
        Mockito.reset(bppServiceClient)
      }

      it("should return internal server error when bpp cancel call fails with an exception") {
        `when`(bppServiceClient.cancel(getCancelRequest())).thenReturn(
          Calls.failure(IOException("Timeout"))
        )

        val response = invokeBppCancel()

        response shouldBeLeft BppError.Internal
        verify(bppServiceClient).cancel(getCancelRequest())
      }

      it("should return internal server error when bpp cancel call returns null value") {
        `when`(bppServiceClient.cancel(getCancelRequest())).thenReturn(
          Calls.response(null)
        )

        val response = invokeBppCancel()

        response shouldBeLeft BppError.NullResponse
        verify(bppServiceClient).cancel(getCancelRequest())
      }

      it("should return internal server error when bpp cancel call returns nack response body") {
        val context = contextFactory.create()
        `when`(bppServiceClient.cancel(getCancelRequest())).thenReturn(
          Calls.response(ProtocolAckResponse(context, ResponseMessage.nack()))
        )

        val response = invokeBppCancel()

        response shouldBeLeft BppError.Nack
        verify(bppServiceClient).cancel(getCancelRequest())
      }
    }
  }

  private fun invokeBppCancel(): Either<BppError, ProtocolAckResponse> {
    val context =
      ClientContext(transactionId = uuidFactory.create(), bppId = "https://bpp1.com")
    return bppCancelService.cancelOrder(
      bppUri = bppUri,
      context = getContext(context.transactionId, context.bppId),
      orderId = "abc",
      cancellationReasonId = "1"
    )
  }

  private fun getCancelRequest(): ProtocolCancelRequest {
    val context =
      ClientContext(transactionId = uuidFactory.create(), bppId = "https://bpp1.com")
    return ProtocolCancelRequest(
      context = getContext(context.transactionId, context.bppId),
      ProtocolCancelRequestMessage(
        orderId = "abc",
        cancellationReasonId = "1"
      )
    )
  }

  private fun getContext(transactionId: String, bppId: String? = null) =
    contextFactory.create(action = ProtocolContext.Action.CANCEL, transactionId = transactionId, bppId = bppId)

}