package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

data class ProtocolItem @Default constructor(
  val id: String? = null,
  val parentItemId: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val price: ProtocolPrice? = null,
  val categoryId: String? = null,
  val locationId: String? = null,
  val time: ProtocolTime? = null,
  val matched: Boolean?,
  val related: Boolean?,
  val recommended: Boolean?,
  val tags: Map<String, String>? = null
)