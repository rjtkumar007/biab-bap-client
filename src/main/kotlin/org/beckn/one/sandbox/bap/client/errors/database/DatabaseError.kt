package org.beckn.one.sandbox.bap.client.errors.database

import org.beckn.one.sandbox.bap.client.errors.HttpError
import org.beckn.one.sandbox.bap.protocol.schemas.Error
import org.beckn.one.sandbox.bap.protocol.schemas.ResponseMessage
import org.springframework.http.HttpStatus

sealed class DatabaseError : HttpError {
  val onWriteError = Error("BAP_006", "Error when writing to DB")

  object OnWrite : DatabaseError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): Error = onWriteError
  }
}