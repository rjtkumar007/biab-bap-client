package org.beckn.one.sandbox.bap.schemas

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable
import org.beckn.one.sandbox.bap.Default

interface ProtocolResponse {
  val context: ProtocolContext
  val error: ProtocolError?
}

data class ResponseMessage @Default constructor(val ack: ProtocolAck) {
  companion object {
    fun ack(): ResponseMessage = ResponseMessage(ProtocolAck(ResponseStatus.ACK))
    fun nack(): ResponseMessage = ResponseMessage(ProtocolAck(ResponseStatus.NACK))
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
  override val error: ProtocolError? = null,
) : ProtocolResponse

@JsonIgnoreProperties(ignoreUnknown = true)
data class ProtocolOnSearch @Default constructor(
  override val context: ProtocolContext,
  val message: ProtocolOnSearchMessage? = null,
  override val error: ProtocolError? = null,
) : ProtocolResponse

data class ProtocolOnSearchMessage @Default constructor(
  val catalog: ProtocolCatalog? = null
)

data class ProtocolOnSelect @Default constructor(
  override val context: ProtocolContext,
  val message: ProtocolOnSelectMessage? = null,
  override val error: ProtocolError? = null
) : ProtocolResponse

data class ProtocolOnSelectMessage @Default constructor(
  val selected: ProtocolOnSelectMessageSelected? = null
)


data class ProtocolOnInit @Default constructor(
  override val context: ProtocolContext,
  val message: ProtocolOnInitMessage? = null,
  override val error: ProtocolError? = null
) : ProtocolResponse

data class ProtocolOnInitMessage @Default constructor(
  val initialized: ProtocolOnInitMessageInitialized? = null
)


data class ProtocolOnConfirm @Default constructor(
  override val context: ProtocolContext,
  val message: ProtocolOnConfirmMessage? = null,
  override val error: ProtocolError? = null
) : ProtocolResponse

data class ProtocolOnConfirmMessage @Default constructor(
  val order: ProtocolOrder? = null
)