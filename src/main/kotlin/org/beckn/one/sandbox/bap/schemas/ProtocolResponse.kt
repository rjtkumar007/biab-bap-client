package org.beckn.one.sandbox.bap.schemas

import kotlinx.serialization.Serializable
import org.beckn.one.sandbox.bap.Default

interface Response {
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

data class ProtocolResponse(
  override val context: ProtocolContext,
  val message: ResponseMessage,
  override val error: Error? = null,
) : Response

data class ProtocolSearchResponse @Default constructor(
  override val context: ProtocolContext,
  val message: ProtocolSearchResponseMessage? = null,
  override val error: Error? = null,
) : Response

data class ProtocolSearchResponseMessage @Default constructor(
  val catalog: ProtocolCatalog? = null
)
