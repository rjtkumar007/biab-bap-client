package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.errors.provider.ProviderError
import org.beckn.one.sandbox.bap.schemas.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

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
  ): Either<ProviderError, ProtocolAckResponse> {
    return try {
      log.info("Initiating select using provider: {}", bppUri)
      val providerServiceClient = bppServiceClientFactory.getClient(bppUri)
      val httpResponse = providerServiceClient.select(
        ProtocolSelectRequest(
          context = context,
          ProtocolSelectRequestMessage(
            selected = ProtocolOnSelectMessageSelected(
              provider = ProtocolProvider(id = providerId, locations = listOf(providerLocation)),
              providerLocation = providerLocation,
              items = items
            )
          ) //todo: this will change from being a straightforward use of func parameters to a mapped version of func parameters(which will be based on client side schema rather than protocol leve
        )
      ).execute()
      log.info("Select response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      when {
        isInternalServerError(httpResponse) -> Either.Left(ProviderError.Internal)
        httpResponse.body() == null -> Either.Left(ProviderError.NullResponse)
        isAckNegative(httpResponse) -> Either.Left(ProviderError.Nack)
        else -> {
          log.info("Successfully invoked select on BPP. Response: {}", httpResponse.body())
          Either.Right(httpResponse.body()!!)
        }
      }
    } catch (e: Exception) {
      log.error("Error when initiating select", e)
      Either.Left(ProviderError.Internal)
    }
  }

  private fun isInternalServerError(httpAckAckResponse: retrofit2.Response<ProtocolAckResponse>) =
    httpAckAckResponse.code() == HttpStatus.INTERNAL_SERVER_ERROR.value()

  private fun isAckNegative(httpAckAckResponse: retrofit2.Response<ProtocolAckResponse>) =
    httpAckAckResponse.body()!!.message.ack.status == ResponseStatus.NACK
}