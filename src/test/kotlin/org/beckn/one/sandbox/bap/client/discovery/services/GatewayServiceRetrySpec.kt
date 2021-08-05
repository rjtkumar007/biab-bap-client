package org.beckn.one.sandbox.bap.client.discovery.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.external.toJson
import org.beckn.one.sandbox.bap.client.shared.dtos.SearchCriteria
import org.beckn.one.sandbox.bap.client.shared.errors.gateway.GatewaySearchError
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.common.factories.MockNetwork
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ResponseMessage
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
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
class GatewayServiceRetrySpec @Autowired constructor(
  val gatewayService: GatewayService,
  val objectMapper: ObjectMapper,
) : DescribeSpec() {
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = mock(UuidFactory::class.java)
  private val contextFactory = ContextFactoryInstance.create(uuidFactory, clock)

  init {
    describe("Search") {
      MockNetwork.startAllSubscribers()
      val gateway = MockNetwork.getRetailBengaluruBg()
      `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      val context = contextFactory.create()

      beforeEach {
        MockNetwork.resetAllSubscribers()
      }

      it("should retry search call if api returns error") {
        val ackResponse = ProtocolAckResponse(context = context, message = ResponseMessage.ack())
        stubGatewaySearchApi(response = WireMock.serverError(), forState = STARTED, nextState = "Success")
        stubGatewaySearchApi(response = WireMock.okJson(objectMapper.toJson(ackResponse)), forState = "Success")


        val response = gatewayService.search(gateway, context, SearchCriteria())

        response
          .fold(
            { Assertions.fail("Search should have been retried but it wasn't. Response: $it") },
            { it.message shouldBe ResponseMessage.ack() }
          )
        verifyGatewaySearchApiIsInvoked(2)
      }

      it("should fail after max retry attempts") {
        stubGatewaySearchApi(response = WireMock.serverError(), forState = STARTED, nextState = "Failure")
        stubGatewaySearchApi(response = WireMock.serverError(), forState = "Failure", nextState = "Failure")

        val response = gatewayService.search(gateway, context, SearchCriteria())

        response shouldBeLeft GatewaySearchError.Internal
        verifyGatewaySearchApiIsInvoked(3)
      }
    }
  }

  private fun verifyGatewaySearchApiIsInvoked(numberOfTimes: Int = 1) {
    MockNetwork.retailBengaluruBg.verify(numberOfTimes, WireMock.postRequestedFor(WireMock.urlEqualTo("/search")))
  }

  private fun stubGatewaySearchApi(
    nextState: String = "Success",
    forState: String = STARTED,
    response: ResponseDefinitionBuilder?
  ) {
    MockNetwork.retailBengaluruBg
      .stubFor(
        WireMock
          .post("/search")
          .inScenario("Retry Scenario")
          .whenScenarioStateIs(forState)
          .willReturn(response)
          .willSetStateTo(nextState)
      )
  }
}
