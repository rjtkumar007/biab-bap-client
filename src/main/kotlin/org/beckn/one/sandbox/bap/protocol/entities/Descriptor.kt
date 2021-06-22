package org.beckn.one.sandbox.bap.protocol.entities

data class Descriptor(
  val name: String?,
  val code: String? = null,
  val symbol: String? = null,
  val shortDesc: String? = null,
  val longDesc: String? = null
)