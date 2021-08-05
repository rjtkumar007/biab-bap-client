package org.beckn.one.sandbox.bap.client.rating.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.external.toJson
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
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
internal class BppServiceRetrySpec @Autowired constructor(
  private val bppService: BppRatingService,
  val objectMapper: ObjectMapper,
) : DescribeSpec() {
  private val clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))
  private val uuidFactory = mock(UuidFactory::class.java)
  private val contextFactory = ContextFactoryInstance.create(uuidFactory, clock)

  init {
    describe("Provide Rating") {
      MockNetwork.startAllSubscribers()
      val bpp = MockNetwork.retailBengaluruBpp
      `when`(uuidFactory.create()).thenReturn("9056ea1b-275d-4799-b0c8-25ae74b6bf51")
      val context = contextFactory.create()

      beforeEach {
        MockNetwork.resetAllSubscribers()
      }

      it("should retry bpp rating api call if api returns error") {
        val ackResponse = ProtocolAckResponse(context = context, message = ResponseMessage.ack())
        stubBppRatingApi(response = WireMock.serverError(), forState = STARTED, nextState = "Success")
        stubBppRatingApi(response = WireMock.okJson(objectMapper.toJson(ackResponse)), forState = "Success")

        val response = bppService.rating(bpp.baseUrl(), context, "item id 1", 2)

        response
          .fold(
            { Assertions.fail("Lookup should have been retried but it wasn't. Response: $it") },
            { it.message shouldBe ackResponse.message }
          )
        verifyBppRatingApiIsInvoked(2)
      }

      it("should fail after max retry attempts") {
        stubBppRatingApi(response = WireMock.serverError(), forState = STARTED, nextState = "Failure")
        stubBppRatingApi(response = WireMock.serverError(), forState = "Failure", nextState = "Failure")

        val response = bppService.rating(bpp.baseUrl(), context, "item id 1", 2)

        response shouldBeLeft BppError.Internal
        verifyBppRatingApiIsInvoked(3)
      }
    }
  }

  private fun stubBppRatingApi(
    nextState: String = "Success",
    forState: String = STARTED,
    response: ResponseDefinitionBuilder?
  ) {
    MockNetwork.retailBengaluruBpp
      .stubFor(
        WireMock.post("/rating")
          .inScenario("Retry Scenario")
          .whenScenarioStateIs(forState)
          .willReturn(response)
          .willSetStateTo(nextState)
      )
  }

  private fun verifyBppRatingApiIsInvoked(numberOfTimes: Int = 1) {
    MockNetwork.retailBengaluruBpp.verify(numberOfTimes, WireMock.postRequestedFor(WireMock.urlEqualTo("/rating")))
  }
}
