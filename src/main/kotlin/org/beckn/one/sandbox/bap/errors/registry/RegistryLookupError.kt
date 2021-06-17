package org.beckn.one.sandbox.bap.errors.registry

import org.beckn.one.sandbox.bap.dtos.Error
import org.beckn.one.sandbox.bap.dtos.ResponseMessage
import org.beckn.one.sandbox.bap.errors.HttpError
import org.springframework.http.HttpStatus

sealed class RegistryLookupError : HttpError {
  val registryError = Error("BAP_001", "Registry lookup returned error")
  val noGatewaysFoundError = Error("BAP_002", "Registry lookup did not return any gateways")

  object RegistryError : RegistryLookupError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): Error = registryError
  }

  object NoGatewayFoundError : RegistryLookupError() {
    override fun status(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR

    override fun message(): ResponseMessage = ResponseMessage.nack()

    override fun error(): Error = noGatewaysFoundError
  }
}
