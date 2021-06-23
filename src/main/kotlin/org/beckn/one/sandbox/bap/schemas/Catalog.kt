package org.beckn.one.sandbox.bap.schemas

data class Catalog (
  val bppProviders: List<ProviderCatalog>? = null,
)

data class ProviderCatalog(
  val id: String? = null,
  val descriptor: Descriptor? = null,
  val categories: List<Category>? = null,
  val items: List<Item>? = null
)