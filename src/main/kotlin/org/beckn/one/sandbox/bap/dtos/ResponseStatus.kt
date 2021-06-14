package org.beckn.one.sandbox.bap.dtos

import kotlinx.serialization.Serializable

@Serializable
enum class ResponseStatus(
  val status: String
) {
  ACK("ACK"),
  NACK("NACK");
}