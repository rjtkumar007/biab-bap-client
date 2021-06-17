package org.beckn.one.sandbox.bap.dtos

data class BecknResponse(
  val context: Context,
  val message: ResponseMessage,
  val error: Error? = null,
)