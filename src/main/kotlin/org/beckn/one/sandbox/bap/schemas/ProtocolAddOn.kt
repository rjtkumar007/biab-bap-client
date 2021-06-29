package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

data class ProtocolAddOn @Default constructor(
  val id: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val price: ProtocolPrice? = null
)
