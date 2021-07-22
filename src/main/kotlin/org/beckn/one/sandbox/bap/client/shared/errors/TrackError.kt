package org.beckn.one.sandbox.bap.client.shared.errors

import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ResponseMessage
import org.springframework.http.HttpStatus

sealed class TrackError : HttpError {
  val bppIdNotPresent =
    ProtocolError("BAP_016", "BPP Id is mandatory")

  object BppIdNotPresent : TrackError() {
    override fun status(): HttpStatus = HttpStatus.BAD_REQUEST
    override fun message(): ResponseMessage = ResponseMessage.nack()
    override fun error(): ProtocolError = bppIdNotPresent
  }
}