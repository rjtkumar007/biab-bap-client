package org.beckn.one.sandbox.bap.services

import arrow.core.Either
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.constants.City
import org.beckn.one.sandbox.bap.constants.Country
import org.beckn.one.sandbox.bap.constants.Domain
import org.beckn.one.sandbox.bap.dtos.BecknResponse
import org.beckn.one.sandbox.bap.dtos.Error
import org.beckn.one.sandbox.bap.dtos.Intent
import org.beckn.one.sandbox.bap.dtos.Request
import org.beckn.one.sandbox.bap.dtos.ResponseMessage.Companion.nack
import org.beckn.one.sandbox.bap.errors.registry.GatewaySearchError
import org.beckn.one.sandbox.bap.external.gateway.GatewayServiceClient
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.NetworkMock
import org.beckn.one.sandbox.bap.factories.UuidFactory
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
      it("should return gateway error when gateway search call fails with an IO exception") {
        NetworkMock.startAllSubscribers()
        val gateway = NetworkMock.getRetailBengaluruBg()
        val queryString = "Fictional mystery books"
        `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
        val searchRequest = Request(
          contextFactory.create(clock),
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
              it.status() shouldBe HttpStatus.INTERNAL_SERVER_ERROR
              it.message() shouldBe nack()
              it.error() shouldBe Error("BAP_003", "Gateway search returned error")
            },
            { Assertions.fail("Search should have timed out but didn't. Response: $it") }
          )
        verify(gatewayServiceClient).search(
          Request(
            contextFactory.create(clock = clock),
            Intent(queryString = queryString)
          )
        )
      }
    }
  }

}
