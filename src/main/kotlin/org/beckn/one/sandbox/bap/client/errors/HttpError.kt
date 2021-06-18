package org.beckn.one.sandbox.bap.client.errors

import org.beckn.one.sandbox.bap.common.dtos.Error
import org.beckn.one.sandbox.bap.common.dtos.ResponseMessage
import org.springframework.http.HttpStatus

interface HttpError {
  fun status(): HttpStatus
  fun message(): ResponseMessage
  fun error(): Error
}
