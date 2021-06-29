package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default


interface BecknResponse {
  val context: Context
  val error: Error?
}

data class SearchResponse @Default constructor(
  override val context: Context,
  val message: SearchResponseMessage? = null,
  override val error: Error? = null
) : BecknResponse

data class SearchResponseMessage @Default constructor(
  val catalog: Catalog? = null
)

data class OnSelect @Default constructor(
  override val context: Context,
  val message: OnSelectMessage? = null,
  override val error: Error? = null
): BecknResponse

data class OnSelectMessage @Default constructor(
  val selected: OnSelectMessageSelected? = null
)