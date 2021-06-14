package org.beckn.one.sandbox.bap.dtos

data class Response(
  val status: ResponseStatus,
  val message_id: String? = null,
  val error: Error? = null
)