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

data class OnInit @Default constructor(
  override val context: Context,
  val message: OnInitMessage? = null,
  override val error: Error? = null
): BecknResponse

data class OnInitMessage @Default constructor(
  val initialized: OnInitMessageInitialized? = null
)

data class OnConfirm @Default constructor(
  override val context: Context,
  val message: OnConfirmMessage? = null,
  override val error: Error? = null
): BecknResponse

data class OnConfirmMessage @Default constructor(
  val order: Order? = null
)