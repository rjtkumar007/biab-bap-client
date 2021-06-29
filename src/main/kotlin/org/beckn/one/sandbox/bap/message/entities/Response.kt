package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default


interface BecknResponse {
  val context: Context
  val error: Error?
}

data class OnSearch @Default constructor(
  override val context: Context,
  val message: OnSearchMessage? = null,
  override val error: Error? = null
) : BecknResponse

data class OnSearchMessage @Default constructor(
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