package org.beckn.one.sandbox.bap.schemas

data class Descriptor(
  val name: String?,
  val code: String? = null,
  val symbol: String? = null,
  val shortDesc: String? = null,
  val longDesc: String? = null
)