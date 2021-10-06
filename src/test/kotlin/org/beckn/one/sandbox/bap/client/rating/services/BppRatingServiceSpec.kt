package org.beckn.one.sandbox.bap.client.rating.services

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

internal class BppRatingServiceSpec : DescribeSpec() {
  private val bppServiceClientFactory = mock(BppClientFactory::class.java)
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = mock(UuidFactory::class.java)
  private val contextFactory = ContextFactoryInstance.create(uuidFactory, clock)
  private val bppService = BppRatingService(bppServiceClientFactory)
  private val bppServiceClient: BppClient = mock(BppClient::class.java)
  private val bppUri = "https://bpp1.com"

  init {
    describe("Provide Rating") {
      `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      `when`(bppServiceClientFactory.getClient(bppUri)).thenReturn(bppServiceClient)
      val trackRequest = getProtocolRatingRequest()

      beforeEach {
        reset(bppServiceClient)
      }

      it("should return bpp internal server error when bpp provide rating call fails with an exception") {
        `when`(bppServiceClient.rating(getProtocolRatingRequest())).thenReturn(
          Calls.failure(IOException("Timeout"))
        )

        val response = invokeBppRatingApi()

        response shouldBeLeft BppError.Internal
        verify(bppServiceClient).rating(getProtocolRatingRequest())
      }

      it("should return bpp internal server error when bpp track call returns null body") {
        `when`(bppServiceClient.rating(trackRequest)).thenReturn(
          Calls.response(null)
        )

        val response = invokeBppRatingApi()

        response shouldBeLeft BppError.NullResponse
        verify(bppServiceClient).rating(getProtocolRatingRequest())
      }

      it("should return bpp internal server error when bpp track call returns nack response body") {
        val context = contextFactory.create(action = ProtocolContext.Action.TRACK)
        `when`(bppServiceClient.rating(trackRequest)).thenReturn(
          Calls.response(ProtocolAckResponse(context, ResponseMessage.nack()))
        )

        val response = invokeBppRatingApi()

        response shouldBeLeft BppError.Nack
        verify(bppServiceClient).rating(getProtocolRatingRequest())
      }
    }
  }

  private fun invokeBppRatingApi(): Either<BppError, ProtocolAckResponse> {
    return bppService.rating(
      bppUri = bppUri,
      context = contextFactory.create(action = ProtocolContext.Action.RATING),
      refId = "item id 1",
      value = 3,
    )
  }

  private fun getProtocolRatingRequest() = ProtocolRatingRequest(
    context = contextFactory.create(action = ProtocolContext.Action.RATING),
    message = ProtocolRatingRequestMessage(id = "item id 1", value = 3),
  )
}
