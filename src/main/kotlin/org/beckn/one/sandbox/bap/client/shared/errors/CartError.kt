package org.beckn.one.sandbox.bap.client.shared.errors

import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ResponseMessage
import org.springframework.http.HttpStatus

sealed class CartError : HttpError {
  val moreThanOneProviderValidationError =
    ProtocolError("BAP_010", "More than one Provider's item(s) selected/initialized")
  val moreThanOneBppValidationError =
    ProtocolError("BAP_014", "More than one BPP's item(s) selected/initialized")
  val invalidPincodeError =
    ProtocolError("BAP_015", "Invalid pincode entered")

  object MultipleBpps : CartError() {
    override fun status(): HttpStatus = HttpStatus.BAD_REQUEST

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = moreThanOneBppValidationError
  }

  object MultipleProviders : CartError() {
    override fun status(): HttpStatus = HttpStatus.BAD_REQUEST

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = moreThanOneProviderValidationError
  }

  object InvalidPincode : CartError() {
    override fun status(): HttpStatus = HttpStatus.BAD_REQUEST

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = invalidPincodeError
  }
}