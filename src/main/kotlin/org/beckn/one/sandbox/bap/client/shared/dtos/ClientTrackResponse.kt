package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ProtocolOnTrackMessageTracking

data class ClientTrackResponse(
  override val context: ProtocolContext,
  val message: ClientTrackResponseMessage? = null,
  override val error: ProtocolError? = null,
) : ClientResponse

data class ClientTrackResponseMessage(
  val tracking: ProtocolOnTrackMessageTracking? = null
)
