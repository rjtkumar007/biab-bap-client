package org.beckn.one.sandbox.bap.client.services

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.external.provider.BppServiceClient
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.common.factories.MockNetwork
import org.beckn.one.sandbox.bap.message.factories.IdFactory
import org.beckn.one.sandbox.bap.message.factories.ProtocolLocationFactory
import org.beckn.one.sandbox.bap.message.factories.ProtocolSelectedItemFactory
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSelectMessageSelected
import org.beckn.one.sandbox.bap.schemas.ProtocolProvider
import org.beckn.one.sandbox.bap.schemas.ProtocolSelectRequest
import org.beckn.one.sandbox.bap.schemas.ProtocolSelectRequestMessage
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito.*
import retrofit2.mock.Calls
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class BppServiceSpec : DescribeSpec() {
  private val providerServiceClientFactory = mock(BppServiceClientFactory::class.java)
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = mock(UuidFactory::class.java)
  private val contextFactory = ContextFactoryInstance.create(uuidFactory, clock)
  private val providerService = BppService(providerServiceClientFactory)
  private val bppServiceClient: BppServiceClient = mock(BppServiceClient::class.java)

  init {
    describe("Select") {
      MockNetwork.startAllSubscribers()
      val provider = MockNetwork.getRetailBengaluruBg()
      `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      `when`(providerServiceClientFactory.getClient(provider.subscriber_url)).thenReturn(bppServiceClient)

      beforeEach {
        MockNetwork.resetAllSubscribers()
        reset(bppServiceClient)
      }

      it("should return provider error when provider select call fails with an IO exception") {
        val selectRequest = getRequest()
        `when`(bppServiceClient.select(selectRequest)).thenReturn(
          Calls.failure(IOException("Timeout"))
        )

        val response = providerService.select(
          contextFactory.create(),
          provider.subscriber_url,
          "paisool",
          ProtocolLocationFactory.addressLocation(1),
          IdFactory.forItems(IdFactory.forProvider(1), 1).map { ProtocolSelectedItemFactory.create(it) }
        )

        response
          .fold(
            { it shouldBe BppError.Internal },
            { Assertions.fail("Provider should have timed out but didn't. Response: $it") }
          )
        verify(bppServiceClient).select(getRequest())
      }
    }
  }

  private fun getRequest() = ProtocolSelectRequest(
    contextFactory.create(),
    ProtocolSelectRequestMessage(
      selected = ProtocolOnSelectMessageSelected(
        provider = ProtocolProvider(id = "paisool", locations = listOf(ProtocolLocationFactory.addressLocation(1))),
        providerLocation = ProtocolLocationFactory.addressLocation(1),
        items = IdFactory.forItems(IdFactory.forProvider(1), 1).map { ProtocolSelectedItemFactory.create(it) }
      )
    )
  )
}
