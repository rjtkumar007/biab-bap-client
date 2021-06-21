package org.beckn.one.sandbox.bap.protocol.schemas

data class Response(
  val context: Context,
  val message: ResponseMessage,
  val error: Error? = null,
)
