package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

data class BecknCatalog @Default constructor(
  val bppProviders: List<ProviderCatalog>? = null,
)

data class ProviderCatalog @Default constructor(
  val id: String? = null,
  val descriptor: Descriptor? = null,
  val categories: List<Category>? = null,
  val items: List<Item>? = null
)