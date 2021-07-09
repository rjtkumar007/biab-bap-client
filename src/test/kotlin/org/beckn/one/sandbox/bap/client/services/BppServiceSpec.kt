package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.external.provider.BppServiceClient
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.message.factories.IdFactory
import org.beckn.one.sandbox.bap.message.factories.ProtocolLocationFactory
import org.beckn.one.sandbox.bap.message.factories.ProtocolSelectedItemFactory
import org.beckn.one.sandbox.bap.schemas.*
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.mockito.Mockito.*
import retrofit2.mock.Calls
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class BppServiceSpec : DescribeSpec() {
  private val bppServiceClientFactory = mock(BppServiceClientFactory::class.java)
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = mock(UuidFactory::class.java)
  private val contextFactory = ContextFactoryInstance.create(uuidFactory, clock)
  private val bppService = BppService(bppServiceClientFactory)
  private val bppServiceClient: BppServiceClient = mock(BppServiceClient::class.java)
  private val bppUri = "https://bpp1.com"

  init {
    describe("Select") {
      `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      `when`(bppServiceClientFactory.getClient(bppUri)).thenReturn(bppServiceClient)
      val selectRequest = getRequest()

      beforeEach {
        reset(bppServiceClient)
      }

      it("should return bpp internal server error when bpp select call fails with an exception") {
        `when`(bppServiceClient.select(getRequest())).thenReturn(
          Calls.failure(IOException("Timeout"))
        )

        val response = invokeBppSelect()

        response shouldBeLeft BppError.Internal
        verify(bppServiceClient).select(getRequest())
      }

      it("should return bpp internal server error when bpp select call returns null body") {
        `when`(bppServiceClient.select(selectRequest)).thenReturn(
          Calls.response(null)
        )

        val response = invokeBppSelect()

        response shouldBeLeft BppError.NullResponse
        verify(bppServiceClient).select(getRequest())
      }

      it("should return bpp internal server error when bpp select call returns nack response body") {
        val context = contextFactory.create()
        `when`(bppServiceClient.select(selectRequest)).thenReturn(
          Calls.response(ProtocolAckResponse(context, ResponseMessage.nack()))
        )

        val response = invokeBppSelect()

        response shouldBeLeft BppError.Nack
        verify(bppServiceClient).select(getRequest())
      }
    }
  }

  private fun invokeBppSelect(): Either<BppError, ProtocolAckResponse> {
    return bppService.select(
      contextFactory.create(),
      bppUri,
      "venugopala stores",
      ProtocolLocationFactory.idLocation(1),
      IdFactory.forItems(IdFactory.forProvider(1), 1).map { ProtocolSelectedItemFactory.create(it) }
    )
  }

  private fun getRequest() = ProtocolSelectRequest(
    contextFactory.create(),
    ProtocolSelectRequestMessage(
      selected = ProtocolOnSelectMessageSelected(
        provider = ProtocolProvider(
          id = "venugopala stores",
          locations = listOf(ProtocolLocationFactory.idLocation(1))
        ),
        items = IdFactory.forItems(IdFactory.forProvider(1), 1).map { ProtocolSelectedItemFactory.create(it) }
      )
    )
  )
}
