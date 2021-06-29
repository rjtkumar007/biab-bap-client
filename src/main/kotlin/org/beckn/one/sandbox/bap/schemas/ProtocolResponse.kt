package org.beckn.one.sandbox.bap.schemas

import kotlinx.serialization.Serializable
import org.beckn.one.sandbox.bap.Default

interface ProtocolResponse {
  val context: ProtocolContext
  val error: Error?
}

data class ResponseMessage @Default constructor(val ack: Ack) {
  companion object {
    fun ack(): ResponseMessage = ResponseMessage(Ack(ResponseStatus.ACK))
    fun nack(): ResponseMessage = ResponseMessage(Ack(ResponseStatus.NACK))
  }
}

@Serializable
enum class ResponseStatus(
  val status: String
) {
  ACK("ACK"),
  NACK("NACK");
}

data class ProtocolAckResponse(
  override val context: ProtocolContext,
  val message: ResponseMessage,
  override val error: Error? = null,
) : ProtocolResponse

data class ProtocolOnSearch @Default constructor(
  override val context: ProtocolContext,
  val message: ProtocolOnSearchMessage? = null,
  override val error: Error? = null,
) : ProtocolResponse

data class ProtocolOnSearchMessage @Default constructor(
  val catalog: ProtocolCatalog? = null
)

data class ProtocolOnSelect @Default constructor(
  override val context: ProtocolContext,
  val message: ProtocolOnSelectMessage? = null,
  override val error: Error? = null
): ProtocolResponse

data class ProtocolOnSelectMessage @Default constructor(
  val selected: ProtocolOnSelectMessageSelected? = null
)

