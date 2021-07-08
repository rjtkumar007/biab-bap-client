package org.beckn.one.sandbox.bap.client.errors.provider

import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.springframework.http.HttpStatus

sealed class ProviderError: HttpError {
  val providerError = ProtocolError("BAP_011", "Provider returned error")
  val nullError = ProtocolError("BAP_012", "Provider returned null")
  val nackError = ProtocolError("BAP_013", "Provider returned nack")

  object Internal : ProviderError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = providerError
  }

  object Nack : ProviderError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = nackError
  }

  object NullResponse : ProviderError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = nullError
  }
}