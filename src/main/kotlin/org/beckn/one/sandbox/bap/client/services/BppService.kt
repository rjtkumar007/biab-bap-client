package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import org.beckn.one.sandbox.bap.client.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.external.provider.BppServiceClient
import org.beckn.one.sandbox.bap.schemas.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import retrofit2.Response

@Service
class BppService @Autowired constructor(
  private val bppServiceClientFactory: BppServiceClientFactory,
  private val log: Logger = LoggerFactory.getLogger(BppService::class.java)
) {
  fun select(
    context: ProtocolContext,
    bppUri: String,
    providerId: String,
    providerLocation: ProtocolLocation,
    items: List<ProtocolSelectedItem>
  ): Either<BppError, ProtocolAckResponse> {
    return Either
      .catch {
        log.info("Invoking Select API on BPP: {}", bppUri)
        val bppServiceClient = bppServiceClientFactory.getClient(bppUri)
        val httpResponse = invokeBppSelectApi(bppServiceClient, context, providerId, providerLocation, items)
        log.info("BPP Select API response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
        return when {
          isInternalServerError(httpResponse) -> Left(BppError.Internal)
          isBodyNull(httpResponse) -> Left(BppError.NullResponse)
          isAckNegative(httpResponse) -> Left(BppError.Nack)
          else -> Right(httpResponse.body()!!)
        }
      }.mapLeft {
        log.error("Error when initiating select", it)
        BppError.Internal
      }
  }

  private fun invokeBppSelectApi(
    providerServiceClient: BppServiceClient,
    context: ProtocolContext,
    providerId: String,
    providerLocation: ProtocolLocation,
    items: List<ProtocolSelectedItem>
  ): Response<ProtocolAckResponse> {
    val selectRequest = ProtocolSelectRequest(
      context = context,
      ProtocolSelectRequestMessage(
        selected = ProtocolOnSelectMessageSelected(
          provider = ProtocolProvider(id = providerId, locations = listOf(providerLocation)),
          providerLocation = providerLocation,
          items = items
        )
      )
    )
    log.info("Select API request body: {}", selectRequest)
    return providerServiceClient.select(selectRequest).execute()
  }

  fun search(
    context: ProtocolContext,
    bppUri: String?,
    providerId: String,
    locationString: String?,
  ): Either<BppError, ProtocolAckResponse> {
    log.info("Initiating search using provider: {}", bppUri)
    return try {
      when (bppUri) {
        null -> Either.Left(BppError.ProviderNotFound)
        else -> {
          val providerServiceClient = bppServiceClientFactory.getClient(bppUri)
          val httpResponse = providerServiceClient.search(
            ProtocolSearchRequest(
              context = context,
              message = ProtocolSearchRequestMessage(
                intent = ProtocolIntent(
                  queryString = null,
                  provider = ProtocolProvider(id = providerId),
                  fulfillment = ProtocolFulfillment(
                    end = ProtocolFulfillmentEnd(
                      location = ProtocolLocation(
                        gps = locationString
                      )
                    )
                  )
                )
              )
            )
          ).execute()
          log.info("Search response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
          when {
            isInternalServerError(httpResponse) -> Either.Left(BppError.Internal)
            httpResponse.body() == null -> Either.Left(BppError.NullResponse)
            isAckNegative(httpResponse) -> Either.Left(BppError.Nack)
            else -> {
              log.info("Successfully invoked search on gateway. Response: {}", httpResponse.body())
              Either.Right(httpResponse.body()!!)
            }
          }
        }
      }
    }
    catch (e: Exception) {
      log.error("Error when initiating search", e)
      Either.Left(BppError.Internal)
    }
  }

  private fun isInternalServerError(httpResponse: Response<ProtocolAckResponse>) =
    httpResponse.code() == HttpStatus.INTERNAL_SERVER_ERROR.value()

  private fun isBodyNull(httpResponse: Response<ProtocolAckResponse>) = httpResponse.body() == null

  private fun isAckNegative(httpResponse: Response<ProtocolAckResponse>) =
    httpResponse.body()!!.message.ack.status == ResponseStatus.NACK
}