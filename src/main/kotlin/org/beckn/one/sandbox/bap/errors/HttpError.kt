package org.beckn.one.sandbox.bap.errors

import org.beckn.one.sandbox.bap.dtos.Response
import org.springframework.http.HttpStatus

interface HttpError {
  fun response(): Response
  fun code(): HttpStatus
}
