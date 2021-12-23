package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ProtocolOnConfirmMessage

data class ClientConfirmResponse(
  override val context: ProtocolContext?,
  val message: ProtocolOnConfirmMessage? = null,
  override val error: ProtocolError? = null,
  var parentOrderId: String? = null,
) : ClientResponse