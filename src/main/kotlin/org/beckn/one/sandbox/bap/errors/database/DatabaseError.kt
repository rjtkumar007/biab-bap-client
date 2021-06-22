package org.beckn.one.sandbox.bap.errors.database

import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.schemas.Error
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.springframework.http.HttpStatus

sealed class DatabaseError : HttpError {
  val onWriteError = Error("BAP_006", "Error when writing to DB")
  val onReadError = Error("BAP_007", "Error when writing to DB")
  val notFoundError = Error("BAP_008", "No message with the given ID")

  object OnWrite : DatabaseError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): Error = onWriteError
  }

  object OnRead : DatabaseError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): Error = onReadError
  }

  object NotFound : DatabaseError() {
    override fun status(): HttpStatus = HttpStatus.NOT_FOUND

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): Error = notFoundError
  }
}