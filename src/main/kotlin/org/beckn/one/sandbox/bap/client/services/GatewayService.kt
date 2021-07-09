package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import org.beckn.one.sandbox.bap.client.errors.gateway.GatewaySearchError
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.schemas.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class GatewayService @Autowired constructor(
  @Value("\${context.domain}") val domain: String,
  @Value("\${context.city}") val city: String,
  @Value("\${context.country}") val country: String,
  @Value("\${context.bap_id}") val bapId: String,
  @Value("\${context.bap_uri}") val bapUri: String,
  val gatewayServiceClientFactory: GatewayServiceClientFactory,
) {
  val log: Logger = LoggerFactory.getLogger(GatewayService::class.java)

  fun search(
    context: ProtocolContext,
    gateway: SubscriberDto,
    queryString: String?,
    locationString: String?
  ): Either<GatewaySearchError, ProtocolAckResponse> {
    return try {
      log.info("Initiating Search using gateway: {}", gateway)
      val gatewayServiceClient = gatewayServiceClientFactory.getClient(gateway)
      log.info("Initiated Search for context: {}", context)
      val httpResponse = gatewayServiceClient.search(
        ProtocolSearchRequest(
          context,
          ProtocolSearchRequestMessage(
            ProtocolIntent(
              queryString = queryString,
              provider = null,
              fulfillment = ProtocolFulfillment(end = ProtocolFulfillmentEnd(location = ProtocolLocation(gps = locationString))),
              item = ProtocolIntentItem(descriptor = ProtocolIntentItemDescriptor(name = queryString))
            )
          )
        )
      ).execute()
      log.info("Search response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      when {
        isInternalServerError(httpResponse) -> Left(GatewaySearchError.Internal)
        httpResponse.body() == null -> Left(GatewaySearchError.NullResponse)
        isAckNegative(httpResponse) -> Left(GatewaySearchError.Nack)
        else -> {
          log.info("Successfully invoked search on gateway. Response: {}", httpResponse.body())
          Right(httpResponse.body()!!)
        }
      }
    } catch (e: Exception) {
      log.error("Error when initiating search", e)
      Left(GatewaySearchError.Internal)
    }
  }

  private fun isInternalServerError(httpAckAckResponse: retrofit2.Response<ProtocolAckResponse>) =
    httpAckAckResponse.code() == HttpStatus.INTERNAL_SERVER_ERROR.value()

  private fun isAckNegative(httpAckAckResponse: retrofit2.Response<ProtocolAckResponse>) =
    httpAckAckResponse.body()!!.message.ack.status == ResponseStatus.NACK

  fun searchProvider(
    context: ProtocolContext,
    gateway: SubscriberDto,
    providerId: String,
    locationString: String?
  ): Either<GatewaySearchError, ProtocolAckResponse> {
    return try {
      log.info("Initiating Search using gateway: {}", gateway)
      val gatewayServiceClient = gatewayServiceClientFactory.getClient(gateway)
      log.info("Initiated Search for context: {}", context)
      val httpResponse = gatewayServiceClient.search(
        ProtocolSearchRequest(
          context,
          ProtocolSearchRequestMessage(
            ProtocolIntent(
              queryString = null,
              provider = ProtocolProvider(id = providerId),
              fulfillment = ProtocolFulfillment(end = ProtocolFulfillmentEnd(location = ProtocolLocation(gps = locationString)))
            )
          )
        )
      ).execute()
      log.info("Search response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      when {
        isInternalServerError(httpResponse) -> Either.Left(GatewaySearchError.Internal)
        httpResponse.body() == null -> Either.Left(GatewaySearchError.NullResponse)
        isAckNegative(httpResponse) -> Either.Left(GatewaySearchError.Nack)
        else -> {
          log.info("Successfully invoked search on gateway. Response: {}", httpResponse.body())
          Either.Right(httpResponse.body()!!)
        }
      }
    } catch (e: Exception) {
      log.error("Error when initiating search", e)
      Left(GatewaySearchError.Internal)
    }
  }
}
