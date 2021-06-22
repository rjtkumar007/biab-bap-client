package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.message.entities.Message

interface BaseResponse {
  val context: Context
  val error: Error?
}

data class Response(
  override val context: Context,
  val message: ResponseMessage,
  override val error: Error? = null,
) : BaseResponse

data class SearchResponse(
  override val context: Context,
  val message: Message? = null,
  override val error: Error? = null,
) : BaseResponse
