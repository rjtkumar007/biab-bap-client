package org.beckn.one.sandbox.bap.client.discovery.services

import arrow.core.Either
import arrow.core.Either.Left
import org.beckn.one.sandbox.bap.client.external.gateway.GatewayClientFactory
import org.beckn.one.sandbox.bap.client.external.hasBody
import org.beckn.one.sandbox.bap.client.external.isAckNegative
import org.beckn.one.sandbox.bap.client.external.isInternalServerError
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.shared.dtos.SearchCriteria
import org.beckn.one.sandbox.bap.client.shared.errors.gateway.GatewaySearchError
import org.beckn.protocol.schemas.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GatewayService @Autowired constructor(
  private val gatewayServiceClientFactory: GatewayClientFactory,
) {
  val log: Logger = LoggerFactory.getLogger(GatewayService::class.java)

  fun search(gateway: SubscriberDto, context: ProtocolContext, criteria: SearchCriteria)
      : Either<GatewaySearchError, ProtocolAckResponse> {
    return Either.catch {
      log.info("Initiating Search using gateway: {}. Context: {}", gateway, context)
      val gatewayServiceClient = gatewayServiceClientFactory.getClient(gateway.subscriber_url)
      val requestBody = buildProtocolSearchRequest(context, criteria)
      val httpResponse = gatewayServiceClient.search(requestBody).execute()
      log.info("Search response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      return when {
        httpResponse.isInternalServerError() -> Left(GatewaySearchError.Internal)
        !httpResponse.hasBody() -> Left(GatewaySearchError.NullResponse)
        httpResponse.isAckNegative() -> Left(GatewaySearchError.Nack)
        else -> {
          log.info("Successfully invoked search on gateway. Response: {}", httpResponse.body())
          Either.Right(httpResponse.body()!!)
        }
      }
    }.mapLeft {
      log.error("Error when initiating search", it)
      GatewaySearchError.Internal
    }
  }

  private fun buildProtocolSearchRequest(context: ProtocolContext, criteria: SearchCriteria) =
    ProtocolSearchRequest(
      context,
      ProtocolSearchRequestMessage(
        ProtocolIntent(
          item = ProtocolIntentItem(descriptor = ProtocolIntentItemDescriptor(name = criteria.searchString)),
          provider = ProtocolProvider( id = criteria.providerId, category_id = criteria.categoryId,
            descriptor = ProtocolDescriptor(name = criteria.providerName)),
          category= ProtocolCategory(
            id = criteria?.categoryId,
            descriptor = ProtocolDescriptor(name = criteria?.categoryName)
          ),
          fulfillment = ProtocolFulfillment(
            start = ProtocolFulfillmentStart(location = ProtocolLocation(gps=criteria.pickupLocation)),
            end = ProtocolFulfillmentEnd(location = ProtocolLocation(gps = criteria.deliveryLocation))),
        )
      )
    )
}
