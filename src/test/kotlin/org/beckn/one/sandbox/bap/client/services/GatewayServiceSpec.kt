package org.beckn.one.sandbox.bap.client.services

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.common.City
import org.beckn.one.sandbox.bap.common.Country
import org.beckn.one.sandbox.bap.common.Domain
import org.beckn.one.sandbox.bap.schemas.Intent
import org.beckn.one.sandbox.bap.schemas.Request
import org.beckn.one.sandbox.bap.schemas.Response
import org.beckn.one.sandbox.bap.schemas.ResponseMessage.Companion.nack
import org.beckn.one.sandbox.bap.client.errors.gateway.GatewaySearchError
import org.beckn.one.sandbox.bap.client.external.gateway.GatewayServiceClient
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.beckn.one.sandbox.bap.common.factories.MockNetwork
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito.*
import retrofit2.mock.Calls
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class GatewayServiceSpec : DescribeSpec() {
  private val queryString = "Fictional mystery books"
  private val gatewayServiceClientFactory = mock(GatewayServiceClientFactory::class.java)
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = mock(UuidFactory::class.java)
  private val gatewayServiceClient: GatewayServiceClient = mock(GatewayServiceClient::class.java)
  private val contextFactory = ContextFactory(
    domain = Domain.LocalRetail.value,
    city = City.Bengaluru.value,
    country = Country.India.value,
    bapId = "beckn_in_a_box_bap",
    bapUrl = "beckn_in_a_box_bap.com",
    uuidFactory = uuidFactory,
    clock = clock
  )
  private val gatewayService: GatewayService =
    GatewayService(
      domain = Domain.LocalRetail.value,
      city = City.Bengaluru.value,
      country = Country.India.value,
      bapId = "beckn_in_a_box_bap",
      bapUrl = "beckn_in_a_box_bap.com",
      gatewayServiceClientFactory = gatewayServiceClientFactory,
      contextFactory = contextFactory
    )

  init {
    describe("Search") {
      MockNetwork.startAllSubscribers()
      val gateway = MockNetwork.getRetailBengaluruBg()
      `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      `when`(gatewayServiceClientFactory.getClient(gateway)).thenReturn(gatewayServiceClient)

      beforeEach {
        MockNetwork.resetAllSubscribers()
        reset(gatewayServiceClient)
      }

      it("should return gateway error when gateway search call fails with an IO exception") {
        val searchRequest = getRequest()
        `when`(gatewayServiceClient.search(searchRequest)).thenReturn(
          Calls.failure(IOException("Timeout"))
        )

        val response = gatewayService.search(gateway, queryString)

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

        val response = gatewayService.search(gateway, queryString)

        response
          .fold(
            { it shouldBe GatewaySearchError.NullResponse },
            { Assertions.fail("Search should have failed due to gateway NACK response but didn't. Response: $it") }
          )
        verify(gatewayServiceClient).search(getRequest())
      }

      it("should return gateway error when gateway search returns negative acknowledgement") {
        val searchRequest = getRequest()
        val nackResponse = Calls.response(Response(contextFactory.create(), nack()))
        `when`(gatewayServiceClient.search(searchRequest)).thenReturn(nackResponse)

        val response = gatewayService.search(gateway, queryString)

        response
          .fold(
            { it shouldBe GatewaySearchError.Nack },
            { Assertions.fail("Search should have failed due to gateway NACK response but didn't. Response: $it") }
          )
        verify(gatewayServiceClient).search(getRequest())
      }
    }
  }

  private fun getRequest() = Request(
    contextFactory.create(),
    Intent(queryString = queryString)
  )

}
