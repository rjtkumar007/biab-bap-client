package org.beckn.one.sandbox.bap.client.errors

import org.beckn.one.sandbox.bap.protocol.schemas.Error
import org.beckn.one.sandbox.bap.protocol.schemas.ResponseMessage
import org.springframework.http.HttpStatus

interface HttpError {
  fun status(): HttpStatus
  fun message(): ResponseMessage
  fun error(): Error
}
