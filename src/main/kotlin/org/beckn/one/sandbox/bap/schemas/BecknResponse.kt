package org.beckn.one.sandbox.bap.schemas

interface Response {
  val context: Context
  val error: Error?
}

data class BecknResponse(
  override val context: Context,
  val message: ResponseMessage,
  override val error: Error? = null,
) : Response

data class SearchResponse(
  override val context: Context,
  val message: List<Catalog>? = null,
  override val error: Error? = null,
) : Response
