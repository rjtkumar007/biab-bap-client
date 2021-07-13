package org.beckn.one.sandbox.bap.client.errors.validation

import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.springframework.http.HttpStatus

sealed class CartError : HttpError {
  val moreThanOneProviderValidationError = ProtocolError("BAP_010", "More than one Provider's item(s) selected")

  object MultipleProviders : CartError() {
    override fun status(): HttpStatus = HttpStatus.BAD_REQUEST

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = moreThanOneProviderValidationError
  }
}