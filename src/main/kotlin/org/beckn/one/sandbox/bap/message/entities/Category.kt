package org.beckn.one.sandbox.bap.message.entities

data class Category(
  val _id: String? = null,
  val id: String? = null,
  val children: List<String> = emptyList(),
  val parentId: String? = null,
  val descriptor: Descriptor? = null
)