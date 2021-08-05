package org.beckn.one.sandbox.bap.client.policy.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.external.provider.BppClientFactory
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolGetCancellationReasonsRequest
import org.beckn.protocol.schemas.ProtocolOption
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import retrofit2.Response

@Service
class BppPolicyService @Autowired constructor(
  private val bppServiceClientFactory: BppClientFactory
) {
  private val log: Logger = LoggerFactory.getLogger(BppPolicyService::class.java)

  fun getCancellationReasons(bppUri: String, context: ProtocolContext): Either<BppError, List<ProtocolOption>> =
    Either.catch {
      log.info("Invoking get cancellation reasons API on BPP: {}", bppUri)
      val bppServiceClient = bppServiceClientFactory.getClient(bppUri)
      val httpResponse = bppServiceClient.getCancellationReasons(
        ProtocolGetCancellationReasonsRequest(
          context = context
        )
      ).execute()
      log.info("BPP get cancellation reasons response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      return when {
        isInternalServerErrorWhenOptionsList(httpResponse) -> Either.Left(BppError.Internal)
        isBodyNullWhenOptionsList(httpResponse) -> Either.Left(BppError.NullResponse)
        else -> Either.Right(httpResponse.body()!!)
      }
    }.mapLeft {
      log.error("Error when invoking BPP get cancellation reasons API", it)
      BppError.Internal
    }

  private fun isInternalServerErrorWhenOptionsList(httpResponse: Response<List<ProtocolOption>>) =
    httpResponse.code() == HttpStatus.INTERNAL_SERVER_ERROR.value()

  private fun isBodyNullWhenOptionsList(httpResponse: Response<List<ProtocolOption>>) =
    httpResponse.body() == null || httpResponse.body()!!
      .isEmpty()

}
