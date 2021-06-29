package org.beckn.one.sandbox.bap.message.entities

data class Offer (
  val id: String? = null,
  val descriptor: Descriptor? = null,
  val locationIds: List<String>? = null,
  val categoryIds: List<String>? = null,
  val itemIds: List<String>? = null,
  val time: Time? = null
)
