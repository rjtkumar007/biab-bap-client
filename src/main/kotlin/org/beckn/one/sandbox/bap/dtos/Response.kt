package org.beckn.one.sandbox.bap.dtos

data class Response(
  val status: ResponseStatus,
  val messageId: String? = null,
  val error: Error? = null
)