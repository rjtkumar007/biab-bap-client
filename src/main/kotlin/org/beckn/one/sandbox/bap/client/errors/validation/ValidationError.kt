package org.beckn.one.sandbox.bap.client.errors.validation

import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.springframework.http.HttpStatus

sealed class ValidationError : HttpError {
  val bppValidationError = ProtocolError("BAP_010", "More than 1 BPP selected for action")

  object MultipleBpp : ValidationError() {
    override fun status(): HttpStatus = HttpStatus.BAD_REQUEST

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = bppValidationError
  }
}