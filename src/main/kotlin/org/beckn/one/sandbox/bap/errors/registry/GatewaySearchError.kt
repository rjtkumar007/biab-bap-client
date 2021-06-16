package org.beckn.one.sandbox.bap.errors.registry

import org.beckn.one.sandbox.bap.dtos.Error
import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.dtos.ResponseStatus
import org.beckn.one.sandbox.bap.errors.HttpError
import org.springframework.http.HttpStatus

sealed class GatewaySearchError : HttpError {
  val gatewayError = Error("BAP_003", "Gateway search returned error")

  object GatewayError : GatewaySearchError() {
    override fun response(): Response =
      Response(status = ResponseStatus.NACK, messageId = null, error = gatewayError)

    override fun code(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
  }
}
