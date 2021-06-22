package org.beckn.one.sandbox.bap.protocol.entities

interface BecknResponse {
  val context: Context
  val error: Error?
}

data class SearchResponse(
  override val context: Context,
  val message: Catalog,
  override val error: Error? = null
): BecknResponse
