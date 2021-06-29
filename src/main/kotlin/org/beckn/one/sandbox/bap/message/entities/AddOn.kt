package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class AddOn @Default constructor(
  val id: String? = null,
  val descriptor: Descriptor? = null,
  val price: Price? = null
)
