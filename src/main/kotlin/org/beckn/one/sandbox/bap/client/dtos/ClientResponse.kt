package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolError

interface ClientResponse {
  val context: ProtocolContext
  val error: ProtocolError?
}

data class ClientErrorResponse(
  override val context: ProtocolContext,
  override val error: ProtocolError? = null
) : ClientResponse