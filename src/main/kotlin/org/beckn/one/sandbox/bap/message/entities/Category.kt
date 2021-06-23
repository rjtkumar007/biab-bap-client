package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class Category @Default constructor(
  val _id: String? = null,
  val id: String? = null,
  val children: List<String> = emptyList(),
  val parentId: String? = null,
  val descriptor: Descriptor? = null
)