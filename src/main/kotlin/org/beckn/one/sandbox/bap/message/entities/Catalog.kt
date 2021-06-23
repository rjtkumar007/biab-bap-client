package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class Catalog @Default constructor(
  val bppProviders: List<ProviderCatalog>? = null,
)

data class ProviderCatalog @Default constructor(
  val id: String? = null,
  val descriptor: Descriptor? = null,
  val categories: List<Category>? = null,
  val items: List<Item>? = null
)