package org.beckn.one.sandbox.bap.errors

import org.beckn.one.sandbox.bap.dtos.Error
import org.beckn.one.sandbox.bap.dtos.ResponseMessage
import org.springframework.http.HttpStatus

interface HttpError {
  fun status(): HttpStatus
  fun message(): ResponseMessage
  fun error(): Error
}
