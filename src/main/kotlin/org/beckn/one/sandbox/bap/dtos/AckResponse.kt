package org.beckn.one.sandbox.bap.dtos

import kotlinx.serialization.Serializable

@Serializable
data class AckResponse constructor(
  val status: Ack.Status,
  val message_id: String
)