package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class Descriptor @Default constructor(
  val name: String?,
  val code: String? = null,
  val symbol: String? = null,
  val shortDesc: String? = null,
  val longDesc: String? = null
)