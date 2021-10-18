package org.beckn.one.sandbox.bap.client.shared.errors

import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ResponseMessage
import org.springframework.http.HttpStatus

sealed class ClientError : HttpError {
  val authenticationError =
    ProtocolError("BAP_401", "Unauthorized access of protected resource, invalid credentials")

  object AuthenticationError : ClientError() {
    override fun status(): HttpStatus = HttpStatus.UNAUTHORIZED

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = authenticationError
  }

}