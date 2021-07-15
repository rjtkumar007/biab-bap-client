package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ProtocolQuotation

data class ClientQuoteResponse(
  override val context: ProtocolContext,
  val message: ClientQuoteResponseMessage?,
  override val error: ProtocolError? = null,
) : ClientResponse

data class ClientQuoteResponseMessage(
  val quote: ProtocolQuotation,
)
