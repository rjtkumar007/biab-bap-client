package org.beckn.one.sandbox.bap.message.entities

data class Item(
  val id: String? = null,
  val parentItemId: String? = null,
  val descriptor: Descriptor? = null,
  val categoryId: String? = null,
  val price: Price? = null,
  val matched: Boolean? = null,
  val related: Boolean? = null,
  val recommended: Boolean? = null,
  val tags: Map<String, String>? = null
)
