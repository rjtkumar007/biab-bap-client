package org.beckn.one.sandbox.bap.client.discovery.services

import arrow.core.Either
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.shared.dtos.SearchCriteria
import org.beckn.one.sandbox.bap.client.shared.errors.gateway.GatewaySearchError
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.common.factories.ResponseFactory
import org.beckn.one.sandbox.bap.common.factories.SubscriberDtoFactory
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.mockito.Mockito.*

internal class SearchServiceSpec : DescribeSpec() {
  private val registryService = mock(RegistryService::class.java)
  private val gatewayService = mock(GatewayService::class.java)
  private val bppSearchService = mock(BppSearchService::class.java)
  private val searchService = SearchService(
    registryService = registryService,
    gatewayService = gatewayService,
    bppSearchService = bppSearchService
  )
  private val context = ContextFactoryInstance.create().create()
  private val criteria = SearchCriteria()
  private val gateway = SubscriberDtoFactory.getDefault(number = 1)
  private val otherGateway = SubscriberDtoFactory.getDefault(number = 2)
  private val successResponse = ResponseFactory.getDefault(context)
  private val internalServerError = GatewaySearchError.Internal
  private val nackError = GatewaySearchError.Nack

  init {
    describe("Search Service") {
      beforeEach {
        reset(registryService)
        reset(gatewayService)
        reset(bppSearchService)
      }

      it("should invoke search api on subsequent gateway instances when calls fail") {
        stubGatewayLookupApi(gateway, otherGateway)
        stubGatewaySearchApi(gateway, Either.Left(internalServerError))
        stubGatewaySearchApi(otherGateway, Either.Right(successResponse))

        val searchResponse = searchService.search(context, criteria)

        searchResponse shouldBeRight successResponse
        verifyGatewaySearchApiInvoked(gateway)
        verifyGatewaySearchApiInvoked(otherGateway)
      }

      it("should not invoke subsequent search apis when call succeeds") {
        stubGatewayLookupApi(gateway, otherGateway)
        stubGatewaySearchApi(gateway, Either.Right(successResponse))

        val searchResponse = searchService.search(context, criteria)

        searchResponse shouldBeRight successResponse
        verifyGatewaySearchApiInvoked(gateway)
        verifyGatewaySearchApiInvoked(otherGateway, numberOfTimes = 0)
      }

      it("should return last gateway error when all gateways return error") {
        stubGatewayLookupApi(gateway, otherGateway)
        stubGatewaySearchApi(gateway, Either.Left(internalServerError))
        stubGatewaySearchApi(otherGateway, Either.Left(nackError))

        val searchResponse = searchService.search(context, criteria)

        searchResponse shouldBeLeft nackError
        verifyGatewaySearchApiInvoked(gateway)
        verifyGatewaySearchApiInvoked(otherGateway)
      }

    }
  }

  private fun verifyGatewaySearchApiInvoked(gateway: SubscriberDto, numberOfTimes: Int = 1) {
    verify(gatewayService, times(numberOfTimes)).search(gateway, context, criteria)
  }

  private fun stubGatewaySearchApi(gateway: SubscriberDto, response: Either<GatewaySearchError, ProtocolAckResponse>) {
    `when`(gatewayService.search(gateway, context, criteria))
      .thenReturn(response)
  }

  private fun stubGatewayLookupApi(gateway: SubscriberDto, anotherGateway: SubscriberDto) {
    `when`(registryService.lookupGateways()).thenReturn(Either.Right(listOf(gateway, anotherGateway)))
  }
}
