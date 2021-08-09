package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ProtocolOnOrderStatusMessage

data class ClientOrderStatusResponse(
  override val context: ProtocolContext,
  val message: ProtocolOnOrderStatusMessage? = null,
  override val error: ProtocolError? = null
) : ClientResponse