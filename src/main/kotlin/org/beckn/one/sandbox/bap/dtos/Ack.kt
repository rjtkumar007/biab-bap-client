package org.beckn.one.sandbox.bap.dtos

import kotlinx.serialization.Serializable

@Serializable
data class Ack(
  val status: Status?
) {
  enum class Status(val value: String) {
    ACK("ACK"),
    NACK("NACK");
  }
}