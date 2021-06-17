package org.beckn.one.sandbox.bap.errors.gateway

import org.beckn.one.sandbox.bap.dtos.Error
import org.beckn.one.sandbox.bap.dtos.ResponseMessage
import org.beckn.one.sandbox.bap.errors.HttpError
import org.springframework.http.HttpStatus

sealed class GatewaySearchError : HttpError {
  val gatewayError = Error("BAP_003", "Gateway search returned error")

  object GatewayError : GatewaySearchError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): Error = gatewayError
  }
}