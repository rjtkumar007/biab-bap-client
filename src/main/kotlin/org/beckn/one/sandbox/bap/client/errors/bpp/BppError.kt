package org.beckn.one.sandbox.bap.client.errors.bpp

import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.springframework.http.HttpStatus

sealed class BppError : HttpError {
  val bppError = ProtocolError("BAP_011", "BPP returned error")
  val nullError = ProtocolError("BAP_012", "BPP returned null")
  val nackError = ProtocolError("BAP_013", "BPP returned nack")

  object Internal : BppError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = bppError
  }

  object Nack : BppError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = nackError
  }

  object NullResponse : BppError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = nullError
  }
}