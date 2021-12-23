package org.beckn.one.sandbox.bap.client.discovery.services

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.external.gateway.GatewayClient
import org.beckn.one.sandbox.bap.client.external.gateway.GatewayClientFactory
import org.beckn.one.sandbox.bap.client.shared.dtos.SearchCriteria
import org.beckn.one.sandbox.bap.client.shared.errors.gateway.GatewaySearchError
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.common.factories.MockNetwork
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.protocol.schemas.*
import org.beckn.protocol.schemas.ResponseMessage.Companion.nack
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito.*
import retrofit2.mock.Calls
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class GatewayServiceSpec : DescribeSpec() {
  private val queryString = "Fictional mystery books"
  private val locationString = "40.741895,-73.989308"
  private val gatewayServiceClientFactory = mock(GatewayClientFactory::class.java)
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = mock(UuidFactory::class.java)
  private val gatewayServiceClient: GatewayClient = mock(GatewayClient::class.java)
  private val contextFactory = ContextFactoryInstance.create(uuidFactory, clock)

  private val gatewayService: GatewayService =
    GatewayService(
      gatewayServiceClientFactory = gatewayServiceClientFactory
    )

  init {
    describe("Search") {
      MockNetwork.startAllSubscribers()
      val gateway = MockNetwork.getRetailBengaluruBg()
      `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      `when`(gatewayServiceClientFactory.getClient(gateway.subscriber_url)).thenReturn(gatewayServiceClient)
      val context = contextFactory.create()

      beforeEach {
        MockNetwork.resetAllSubscribers()
        reset(gatewayServiceClient)
      }

      it("should return gateway error when gateway search call fails with an IO exception") {
        val searchRequest = getRequest()
        `when`(gatewayServiceClient.search(searchRequest)).thenReturn(
          Calls.failure(IOException("Timeout"))
        )

        val response = gatewayService.search(
          gateway, context,
          SearchCriteria(searchString = queryString, deliveryLocation = locationString,pickupLocation = locationString,
            categoryId = "fruits",providerId = "padma coffee works")
        )

        response
          .fold(
            { it shouldBe GatewaySearchError.Internal },
            { Assertions.fail("Search should have timed out but didn't. Response: $it") }
          )
        verify(gatewayServiceClient).search(getRequest())
      }

      it("should return gateway error when gateway search returns null response") {
        val searchRequest = getRequest()
        `when`(gatewayServiceClient.search(searchRequest)).thenReturn(Calls.response(null))

        val response = gatewayService.search(
          gateway, context,
          SearchCriteria(searchString = queryString, deliveryLocation = locationString,
            pickupLocation = locationString, categoryId = "fruits",providerId = "padma coffee works")
        )

        response
          .fold(
            { it shouldBe GatewaySearchError.NullResponse },
            { Assertions.fail("Search should have failed due to gateway NACK response but didn't. Response: $it") }
          )
        verify(gatewayServiceClient).search(getRequest())
      }

      it("should return gateway error when gateway search returns negative acknowledgement") {
        val searchRequest = getRequest()
        val nackResponse = Calls.response(ProtocolAckResponse(context, nack()))
        `when`(gatewayServiceClient.search(searchRequest)).thenReturn(nackResponse)

        val response = gatewayService.search(
          gateway, context,
          SearchCriteria(searchString = queryString, deliveryLocation = locationString, pickupLocation = locationString,
            providerId = "padma coffee works",
            categoryId = "fruits")
        )

        response
          .fold(
            { it shouldBe GatewaySearchError.Nack },
            { Assertions.fail("Search should have failed due to gateway NACK response but didn't. Response: $it") }
          )
        verify(gatewayServiceClient).search(getRequest())
      }
    }
  }

  private fun getRequest() = ProtocolSearchRequest(
    contextFactory.create(),
    ProtocolSearchRequestMessage(
      ProtocolIntent(
        fulfillment = ProtocolFulfillment(
          start = ProtocolFulfillmentStart(location = ProtocolLocation(gps = locationString)),
          end = ProtocolFulfillmentEnd(location = ProtocolLocation(gps = locationString))
        ),
        category= ProtocolCategory(
          id = "fruits",
          descriptor = ProtocolDescriptor(name = null)
        ),
        item = ProtocolIntentItem(descriptor = ProtocolIntentItemDescriptor(name = queryString)),
        provider = ProtocolProvider(id = "padma coffee works",category_id= "fruits", descriptor = ProtocolDescriptor(name = null))
      )
    )
  )

}
