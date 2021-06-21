package org.beckn.one.sandbox.bap.common.dtos

data class Response(
  val context: Context,
  val message: ResponseMessage,
  val error: Error? = null,
)
