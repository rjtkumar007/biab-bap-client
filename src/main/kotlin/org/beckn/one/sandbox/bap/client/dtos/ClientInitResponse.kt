package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ProtocolOnInitMessage

data class ClientInitResponse(
  override val context: ProtocolContext,
  val message: ProtocolOnInitMessage? = null,
  override val error: ProtocolError? = null
): ClientResponse
