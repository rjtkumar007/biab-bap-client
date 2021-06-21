package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import org.beckn.one.sandbox.bap.client.errors.gateway.GatewaySearchError
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.common.dtos.Intent
import org.beckn.one.sandbox.bap.common.dtos.Request
import org.beckn.one.sandbox.bap.common.dtos.Response
import org.beckn.one.sandbox.bap.common.dtos.ResponseStatus
import org.beckn.one.sandbox.bap.common.factories.ContextFactory
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
  @Value("\${context.bap_url}") val bapUrl: String,
  val gatewayServiceClientFactory: GatewayServiceClientFactory,
  val contextFactory: ContextFactory
) {
  val log: Logger = LoggerFactory.getLogger(GatewayService::class.java)

  fun search(gateway: SubscriberDto, queryString: String?): Either<GatewaySearchError, Response> {
    return try {
      log.info("Initiating Search using gateway: {}", gateway)
      val gatewayServiceClient = gatewayServiceClientFactory.getClient(gateway)
      val httpResponse = gatewayServiceClient.search(
        Request(contextFactory.create(), Intent(queryString = queryString))
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

  private fun isInternalServerError(httpResponse: retrofit2.Response<Response>) =
    httpResponse.code() == HttpStatus.INTERNAL_SERVER_ERROR.value()

  private fun isAckNegative(httpResponse: retrofit2.Response<Response>) =
    httpResponse.body()!!.message.ack.status == ResponseStatus.NACK
}
