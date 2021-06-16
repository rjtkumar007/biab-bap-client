package org.beckn.one.sandbox.bap.services

import arrow.core.Either
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.constants.City
import org.beckn.one.sandbox.bap.constants.Country
import org.beckn.one.sandbox.bap.constants.Domain
import org.beckn.one.sandbox.bap.dtos.*
import org.beckn.one.sandbox.bap.errors.registry.GatewaySearchError
import org.beckn.one.sandbox.bap.external.gateway.GatewayServiceClient
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.NetworkMock
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import retrofit2.mock.Calls
import java.io.IOException
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

internal class GatewayServiceSpec : DescribeSpec() {
  private val gatewayServiceClient: GatewayServiceClient = mock(GatewayServiceClient::class.java)
  private val gatewayServiceClientFactory = mock(GatewayServiceClientFactory::class.java)
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))

  private val gatewayService: GatewayService =
    GatewayService(
      domain = Domain.LocalRetail.value,
      city = City.Bengaluru.value,
      country = Country.India.value,
      bapId = "beckn_in_a_box_bap",
      bapUrl = "beckn_in_a_box_bap.com",
      gatewayServiceClientFactory = gatewayServiceClientFactory,
      clock = clock
    )

  init {
    describe("Search") {
      it("should return gateway error when gateway search call fails with an IO exception") {
        NetworkMock.startAllSubscribers()
        val gateway = NetworkMock.getRetailBengaluruBg()
        val queryString = "Fictional mystery books"
        val searchRequest = Request(
          ContextFactory.getDefaultContext(clock),
          Intent(queryString = queryString)
        )
        `when`(gatewayServiceClientFactory.getClient(gateway)).thenReturn(gatewayServiceClient)
        `when`(gatewayServiceClient.search(searchRequest)).thenReturn(
          Calls.failure(IOException("Timeout"))
        )

        val response: Either<GatewaySearchError, BecknResponse> =
          gatewayService.search(gateway, queryString)

        response
          .fold(
            {
              it.code() shouldBe HttpStatus.INTERNAL_SERVER_ERROR
              it.response() shouldBe Response(
                status = ResponseStatus.NACK,
                error = Error("BAP_003", "Gateway search returned error")
              )
            },
            { Assertions.fail("Search should have timed out but didn't. Response: $it") }
          )
        verify(gatewayServiceClient).search(
          Request(
            ContextFactory.getDefaultContext(clock = clock),
            Intent(queryString = queryString)
          )
        )
      }
    }
  }

}
