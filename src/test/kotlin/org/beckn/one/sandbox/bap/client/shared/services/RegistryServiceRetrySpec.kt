package org.beckn.one.sandbox.bap.client.shared.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.external.gateway.GatewayClient
import org.beckn.one.sandbox.bap.client.shared.errors.gateway.GatewaySearchError
import org.beckn.one.sandbox.bap.client.shared.errors.registry.RegistryLookupError
import org.beckn.one.sandbox.bap.common.factories.MockNetwork
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class RegistryServiceRetrySpec @Autowired constructor(
  val registryService: RegistryService,
  val objectMapper: ObjectMapper,
) : DescribeSpec() {
//  private val uuidFactory = mock(UuidFactory::class.java)
  private val gatewayServiceClient: GatewayClient = mock(GatewayClient::class.java)

  init {
    describe("Search") {
      MockNetwork.startAllSubscribers()
//      `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      val allGateways = MockNetwork.getAllGateways()

      beforeEach {
        MockNetwork.resetAllSubscribers()
        reset(gatewayServiceClient)
      }

      it("should retry search call if api returns error") {
        stubRegistryLookupApi(response = serverError())
        stubRegistryLookupApi(forState = "Success", response = okJson(toJson(allGateways)))

        val response = registryService.lookupGateways()

        response
          .fold(
            { Assertions.fail("Lookup should have been retried but it wasn't. Response: $it") },
            { it shouldBe allGateways }
          )
        MockNetwork.registry.verify(2, postRequestedFor(urlEqualTo("/lookup")))
      }

      it("should fail after max retry attempts") {
        stubRegistryLookupApi(response = serverError(), forState = STARTED, nextState = "Failure")
        stubRegistryLookupApi(response = serverError(), forState = "Failure", nextState = "Failure")

        val response = registryService.lookupGateways()

        response shouldBeLeft RegistryLookupError.Internal
        verifyGatewaySearchApiIsInvoked(3)
      }
    }
  }

  private fun verifyGatewaySearchApiIsInvoked(numberOfTimes: Int = 1) {
    MockNetwork.registry.verify(numberOfTimes, postRequestedFor(urlEqualTo("/lookup")))
  }

  private fun stubRegistryLookupApi(
    forState: String = STARTED,
    nextState: String = "Success",
    response: ResponseDefinitionBuilder?
  ) {
    MockNetwork.registry
      .stubFor(
        post("/lookup")
          .inScenario("Retry Scenario")
          .whenScenarioStateIs(forState)
          .willReturn(response)
          .willSetStateTo(nextState)
      )
  }

  private fun toJson(instance: Any) =
    objectMapper.writeValueAsString(instance)

}
