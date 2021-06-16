package org.beckn.one.sandbox.bap.errors.registry

import org.beckn.one.sandbox.bap.dtos.Error
import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.dtos.ResponseStatus
import org.beckn.one.sandbox.bap.errors.HttpError
import org.springframework.http.HttpStatus

sealed class RegistryLookupError : HttpError {
  val registryError = Error("BAP_001", "Registry lookup returned error")
  val noGatewaysFoundError = Error("BAP_002", "Registry lookup did not return any gateways")

  object RegistryError : RegistryLookupError() {
    override fun response(): Response =
      Response(status = ResponseStatus.NACK, messageId = null, error = registryError)

    override fun code(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
  }

  object NoGatewayFoundError : RegistryLookupError() {
    override fun response(): Response =
      Response(status = ResponseStatus.NACK, messageId = null, error = noGatewaysFoundError)

    override fun code(): HttpStatus = HttpStatus.INTERNAL_SERVER_ERROR
  }
}
