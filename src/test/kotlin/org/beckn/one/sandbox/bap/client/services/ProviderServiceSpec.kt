package org.beckn.one.sandbox.bap.client.services

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.errors.provider.ProviderError
import org.beckn.one.sandbox.bap.client.external.provider.ProviderServiceClient
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
import org.mockito.Mockito
import retrofit2.mock.Calls
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class ProviderServiceSpec : DescribeSpec() {
  private val providerServiceClientFactory = Mockito.mock(ProviderServiceClientFactory::class.java)
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = Mockito.mock(UuidFactory::class.java)
  private val contextFactory = ContextFactoryInstance.create(uuidFactory, clock)
  private val providerService = ProviderService(providerServiceClientFactory, contextFactory)
  private val providerServiceClient: ProviderServiceClient = Mockito.mock(ProviderServiceClient::class.java)

  init {
    describe("Select") {
      MockNetwork.startAllSubscribers()
      val provider = MockNetwork.getRetailBengaluruBg()
      Mockito.`when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      Mockito.`when`(providerServiceClientFactory.getClient(provider)).thenReturn(providerServiceClient)

      beforeEach {
        MockNetwork.resetAllSubscribers()
        Mockito.reset(providerServiceClient)
      }

      it("should return provider error when provider select call fails with an IO exception") {
        val selectRequest = getRequest()
        Mockito.`when`(providerServiceClient.select(selectRequest)).thenReturn(
          Calls.failure(IOException("Timeout"))
        )

        val response = providerService.select(
          provider,
          "paisool",
          ProtocolLocationFactory.addressLocation(1),
          IdFactory.forItems(IdFactory.forProvider(1), 1).map { ProtocolSelectedItemFactory.create(it) })

        response
          .fold(
            { it shouldBe ProviderError.Internal },
            { Assertions.fail("Provider should have timed out but didn't. Response: $it") }
          )
        Mockito.verify(providerServiceClient).select(getRequest())
      }
    }
  }

  private fun getRequest() = ProtocolSelectRequest(
    contextFactory.create(),
    ProtocolSelectRequestMessage(
      selected = ProtocolOnSelectMessageSelected(
        provider = ProtocolProvider(id = "paisool"),
        providerLocation = ProtocolLocationFactory.addressLocation(1),
        items = IdFactory.forItems(IdFactory.forProvider(1), 1).map { ProtocolSelectedItemFactory.create(it) }
      )
    )
  )
}
