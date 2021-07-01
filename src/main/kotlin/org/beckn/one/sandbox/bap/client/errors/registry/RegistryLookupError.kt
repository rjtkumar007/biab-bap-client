package org.beckn.one.sandbox.bap.client.errors.registry

import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.beckn.one.sandbox.bap.errors.HttpError
import org.springframework.http.HttpStatus

sealed class RegistryLookupError : HttpError {
  val registryError = ProtocolError("BAP_001", "Registry lookup returned error")
  val noGatewaysFoundError = ProtocolError("BAP_002", "Registry lookup did not return any gateways")

  object Internal : RegistryLookupError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = registryError
  }

  object NoGatewayFound : RegistryLookupError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): ProtocolError = noGatewaysFoundError
  }
}
