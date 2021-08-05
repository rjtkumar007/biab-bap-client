package org.beckn.one.sandbox.bap.client.shared.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.client.external.hasBody
import org.beckn.one.sandbox.bap.client.external.isInternalServerError
import org.beckn.one.sandbox.bap.client.shared.errors.bap.ProtocolClientError
import org.beckn.protocol.schemas.ProtocolResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import retrofit2.Call

@Service
class GenericProtocolClientService<Proto : ProtocolResponse> {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  fun getResponse(call: Call<List<Proto>>): Either<ProtocolClientError, List<Proto>> =
    Either.catch {
      val httpResponse = call.execute()
      return when {
        httpResponse.isSuccessful -> Either.Right(httpResponse.body().orEmpty())
        httpResponse.isInternalServerError() -> Either.Left(ProtocolClientError.Internal)
        !httpResponse.hasBody() -> Either.Left(ProtocolClientError.NullResponse)
        else -> Either.Left(ProtocolClientError.Internal)
      }
    }.mapLeft {
      log.error("Error when invoking api", it)
      ProtocolClientError.Internal
    }

}