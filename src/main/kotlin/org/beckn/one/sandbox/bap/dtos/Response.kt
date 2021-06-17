package org.beckn.one.sandbox.bap.dtos

data class Response(
  val context: Context,
  val message: ResponseMessage,
  val error: Error? = null,
)