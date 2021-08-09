package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ProtocolOnCancelMessage

data class ClientCancelResponse(
  override val context: ProtocolContext,
  val message: ProtocolOnCancelMessage? = null,
  override val error: ProtocolError? = null
): ClientResponse