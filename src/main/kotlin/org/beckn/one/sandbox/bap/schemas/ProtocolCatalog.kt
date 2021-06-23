package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

data class ProtocolCatalog @Default constructor(
  val bppProviders: List<ProtocolProviderCatalog>? = null,
)

data class ProtocolProviderCatalog @Default constructor(
  val id: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val categories: List<ProtocolCategory>? = null,
  val items: List<ProtocolItem>? = null
)