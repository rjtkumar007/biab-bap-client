package org.beckn.one.sandbox.bap.client.discovery.services

import arrow.core.Either
import arrow.core.Either.Left
import io.github.resilience4j.core.IntervalFunction
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.beckn.one.sandbox.bap.client.external.gateway.GatewayClientFactory
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.shared.dtos.SearchCriteria
import org.beckn.one.sandbox.bap.client.shared.errors.gateway.GatewaySearchError
import org.beckn.protocol.schemas.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.server.WebServerException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.TimeoutException

@Service
class GatewayService @Autowired constructor(
  val gatewayServiceClientFactory: GatewayClientFactory,
) {
  val log: Logger = LoggerFactory.getLogger(GatewayService::class.java)

  private val retry: Retry = Retry.of("id", RetryConfig.custom<Response<String>>()
    .maxAttempts(3)
    .intervalFunction(
      IntervalFunction
        .ofExponentialBackoff(1000, 1.0)
    )
    .retryOnResult { response -> response.code() == 500 }
    .retryOnException { e -> e is WebServerException }
    .retryExceptions(IOException::class.java, TimeoutException::class.java)
    .failAfterMaxAttempts(true)
    .build())

  fun search(gateway: SubscriberDto, context: ProtocolContext, criteria: SearchCriteria)
      : Either<GatewaySearchError, ProtocolAckResponse> {
    return Either.catch {
      log.info("Initiating Search using gateway: {}. Context: {}", gateway, context)
      val gatewayServiceClient = gatewayServiceClientFactory.getClient(gateway.subscriber_url)
      val httpResponse = gatewayServiceClient.search(buildProtocolSearchRequest(context, criteria)).execute()
      log.info("Search response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      return when {
        isInternalServerError(httpResponse) -> Left(GatewaySearchError.Internal)
        httpResponse.body() == null -> Left(GatewaySearchError.NullResponse)
        isAckNegative(httpResponse) -> Left(GatewaySearchError.Nack)
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
          provider = ProtocolProvider(id = criteria.providerId),
          fulfillment = ProtocolFulfillment(end = ProtocolFulfillmentEnd(location = ProtocolLocation(gps = criteria.deliveryLocation))),
        )
      )
    )

  private fun isInternalServerError(httpAckAckResponse: retrofit2.Response<ProtocolAckResponse>) =
    httpAckAckResponse.code() == HttpStatus.INTERNAL_SERVER_ERROR.value()

  private fun isAckNegative(httpAckAckResponse: retrofit2.Response<ProtocolAckResponse>) =
    httpAckAckResponse.body()!!.message.ack.status == ResponseStatus.NACK
}
