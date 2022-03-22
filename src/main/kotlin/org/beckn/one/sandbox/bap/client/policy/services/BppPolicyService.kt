package org.beckn.one.sandbox.bap.client.policy.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.external.hasBody
import org.beckn.one.sandbox.bap.client.external.isAckNegative
import org.beckn.one.sandbox.bap.client.external.isInternalServerError
import org.beckn.one.sandbox.bap.client.external.provider.BppClientFactory
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.protocol.schemas.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import retrofit2.Response

@Service
class BppPolicyService @Autowired constructor(
  private val bppServiceClientFactory: BppClientFactory
) {
  private val log: Logger = LoggerFactory.getLogger(BppPolicyService::class.java)

  fun getCancellationReasons(bppUri: String, context: ProtocolContext): Either<BppError, ProtocolAckResponse> =
    Either.catch {
      log.info("Invoking get cancellation reasons API on BPP: {}", bppUri)
      val bppServiceClient = bppServiceClientFactory.getClient(bppUri)
      val httpResponse = bppServiceClient.getCancellationReasons(
        ProtocolGetPolicyRequest(
          context = context
        )
      ).execute()
      log.info("BPP get cancellation reasons response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())

      return when {
        httpResponse.isInternalServerError() -> Either.Left(BppError.Internal)
        !httpResponse.hasBody() -> Either.Left(BppError.NullResponse)
        httpResponse.isAckNegative() -> Either.Left(BppError.Nack)
        else -> Either.Right(httpResponse.body()!!)
      }
    }.mapLeft {
      log.error("Error when invoking BPP get cancellation reasons API", it)
      BppError.Internal
    }

  fun getRatingCategories(bppUri: String, context: ProtocolContext): Either<BppError, List<ProtocolRatingCategory>> =
    Either.catch {
      log.info("Invoking get rating categories API on BPP: {}", bppUri)
      val bppServiceClient = bppServiceClientFactory.getClient(bppUri)
      val httpResponse = bppServiceClient.getRatingCategories(
        ProtocolGetPolicyRequest(
          context = context
        )
      ).execute()
      log.info("BPP get rating categories response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      return when{
        httpResponse.isInternalServerError() -> Either.Left(BppError.Internal)
        !httpResponse.hasBody() || hasEmptyBody(httpResponse) -> Either.Left(BppError.NullResponse)
        else -> Either.Right(httpResponse.body()!!)
      }
    }.mapLeft {
      log.error("Error when invoking BPP get rating categories reasons API", it)
      BppError.Internal
    }

  private fun <T> hasEmptyBody(httpResponse: Response<List<T>>) =
    httpResponse.body()!!.isEmpty()

}
