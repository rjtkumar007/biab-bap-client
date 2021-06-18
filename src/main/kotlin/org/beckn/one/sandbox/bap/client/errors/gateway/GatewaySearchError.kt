package org.beckn.one.sandbox.bap.client.errors.gateway

import org.beckn.one.sandbox.bap.common.dtos.Error
import org.beckn.one.sandbox.bap.common.dtos.ResponseMessage
import org.beckn.one.sandbox.bap.client.errors.HttpError
import org.springframework.http.HttpStatus

sealed class GatewaySearchError : HttpError {
  val gatewayError = Error("BAP_003", "Gateway search returned error")
  val nullError = Error("BAP_004", "Gateway search returned null")
  val nackError = Error("BAP_005", "Gateway search returned nack")

  object Internal : GatewaySearchError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): Error = gatewayError
  }

  object Nack : GatewaySearchError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): Error = nackError
  }

  object NullResponse : GatewaySearchError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): Error = nullError
  }
}