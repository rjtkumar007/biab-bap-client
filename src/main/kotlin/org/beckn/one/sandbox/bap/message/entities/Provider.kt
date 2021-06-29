package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class Provider @Default constructor(
  val id: String? = null,
  val descriptor: Descriptor? = null,
  val time: Time? = null,
  val locations: List<Location>? = null,
  val tags: Map<String, String>? = null
)
