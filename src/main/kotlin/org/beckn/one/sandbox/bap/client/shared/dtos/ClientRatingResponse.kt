package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ProtocolOnRatingMessageFeedback

data class ClientRatingResponse(
  override val context: ProtocolContext,
  val message: ClientRatingResponseMessage? = null,
  override val error: ProtocolError? = null,
) : ClientResponse

data class ClientRatingResponseMessage(
  val feedback: ProtocolOnRatingMessageFeedback? = null
)
