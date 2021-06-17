package org.beckn.one.sandbox.bap.services

import arrow.core.Either
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.constants.City
import org.beckn.one.sandbox.bap.constants.Country
import org.beckn.one.sandbox.bap.constants.Domain
import org.beckn.one.sandbox.bap.dtos.Intent
import org.beckn.one.sandbox.bap.dtos.Request
import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.errors.gateway.GatewaySearchError
import org.beckn.one.sandbox.bap.external.gateway.GatewayServiceClient
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.NetworkMock
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito.*
import retrofit2.mock.Calls
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class GatewayServiceSpec : DescribeSpec() {
  private val gatewayServiceClient: GatewayServiceClient = mock(GatewayServiceClient::class.java)
  private val gatewayServiceClientFactory = mock(GatewayServiceClientFactory::class.java)
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = mock(UuidFactory::class.java)
  private val contextFactory = ContextFactory(
    domain = Domain.LocalRetail.value,
    city = City.Bengaluru.value,
    country = Country.India.value,
    bapId = "beckn_in_a_box_bap",
    bapUrl = "beckn_in_a_box_bap.com",
    uuidFactory = uuidFactory
  )

  private val gatewayService: GatewayService =
    GatewayService(
      domain = Domain.LocalRetail.value,
      city = City.Bengaluru.value,
      country = Country.India.value,
      bapId = "beckn_in_a_box_bap",
      bapUrl = "beckn_in_a_box_bap.com",
      gatewayServiceClientFactory = gatewayServiceClientFactory,
      clock = clock,
      contextFactory = contextFactory
    )

  init {
    describe("Search") {
      NetworkMock.startAllSubscribers()
      val gateway = NetworkMock.getRetailBengaluruBg()
      val queryString = "Fictional mystery books"
      `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      `when`(gatewayServiceClientFactory.getClient(gateway)).thenReturn(gatewayServiceClient)

      beforeEach {
        NetworkMock.resetAllSubscribers()
      }

      it("should return gateway error when gateway search call fails with an IO exception") {
        val searchRequest = getRequest(queryString)
        `when`(gatewayServiceClient.search(searchRequest)).thenReturn(
          Calls.failure(IOException("Timeout"))
        )

        val response: Either<GatewaySearchError, Response> =
          gatewayService.search(gateway, queryString)

        response
          .fold(
            { it shouldBe GatewaySearchError.GatewayError },
            { Assertions.fail("Search should have timed out but didn't. Response: $it") }
          )
        verify(gatewayServiceClient).search(
          getRequest(queryString)
        )
      }
    }
  }

  private fun getRequest(queryString: String) = Request(
    contextFactory.create(clock),
    Intent(queryString = queryString)
  )

}
