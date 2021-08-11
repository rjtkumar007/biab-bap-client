package org.beckn.one.sandbox.bap.client.shared.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.external.toJson
import org.beckn.one.sandbox.bap.common.factories.MockNetwork
import org.beckn.one.sandbox.bap.common.factories.SubscriberDtoFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class RegistryServiceCacheSpec @Autowired constructor(
  val registryService: RegistryService,
  val objectMapper: ObjectMapper,
) : DescribeSpec() {
  init {

    describe("Lookup Gateways") {
      MockNetwork.startAllSubscribers()

      beforeEach {
        MockNetwork.resetAllSubscribers()
      }

      it("should cache gateway response") {
        val allGateways = listOf(
          SubscriberDtoFactory.getDefault(1),
          SubscriberDtoFactory.getDefault(2)
        )
        stubRegistryLookupApi(subscribers = allGateways)

        val firstResponse = registryService.lookupGateways()
        val secondResponse = registryService.lookupGateways()

        firstResponse shouldBeRight allGateways
        firstResponse shouldBe secondResponse
        MockNetwork.registry.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/lookup")))
      }
    }

    describe("Lookup BPP by Id") {
      MockNetwork.startAllSubscribers()
      beforeEach {
        MockNetwork.resetAllSubscribers()
        MockNetwork.registryBppLookupApi.resetAll()
      }

      it("should cache multiple bpp lookup responses by their id") {
        val bpp = MockNetwork.getRetailBengaluruBpp()
        val anotherBpp = MockNetwork.getAnotherRetailBengaluruBpp()
        stubRegistryLookupApi(
          MockNetwork.registryBppLookupApi,
          listOf(bpp),
          forState = STARTED,
          nextState = "anotherBpp"
        )
        stubRegistryLookupApi(
          MockNetwork.registryBppLookupApi,
          listOf(anotherBpp),
          forState = "anotherBpp",
          nextState = "anotherBpp"
        )

        val bppFirstResponse = registryService.lookupBppById(bpp.subscriber_id)
        val bppSecondResponse = registryService.lookupBppById(bpp.subscriber_id)
        val anotherBppFirstResponse = registryService.lookupBppById(anotherBpp.subscriber_id)
        val anotherBppSecondResponse = registryService.lookupBppById(anotherBpp.subscriber_id)

        bppFirstResponse shouldBeRight listOf(bpp)
        bppFirstResponse shouldBe bppSecondResponse
        anotherBppFirstResponse shouldBeRight listOf(anotherBpp)
        anotherBppFirstResponse shouldBe anotherBppSecondResponse

        MockNetwork.registryBppLookupApi.verify(2, WireMock.postRequestedFor(WireMock.urlEqualTo("/lookup")))
      }
    }
  }

  private fun stubRegistryLookupApi(
    registry: WireMockServer = MockNetwork.registry,
    subscribers: List<SubscriberDto>,
    forState: String = STARTED,
    nextState: String = "Next State",
  ) {
    registry
      .stubFor(
        WireMock.post("/lookup")
          .inScenario("Cache Scenario")
          .whenScenarioStateIs(forState)
          .willSetStateTo(nextState)
          .willReturn(WireMock.okJson(objectMapper.toJson(subscribers)))
      )
  }
}