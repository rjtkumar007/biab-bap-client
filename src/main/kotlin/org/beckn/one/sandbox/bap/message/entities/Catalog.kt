package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default
import java.time.LocalDateTime

data class Catalog @Default constructor(
  val bppDescriptor: Descriptor? = null,
  val bppProviders: List<ProviderCatalog>? = null,
  val bppCategories: List<Category>? = null,
  val exp: LocalDateTime? = null
)

data class ProviderCatalog @Default constructor(
  val id: String? = null,
  val descriptor: Descriptor? = null,
  val locations: List<Location>? = null,
  val categories: List<Category>? = null,
  val items: List<Item>? = null,
  val tags: Map<String, String>? = null,
  val exp: LocalDateTime? = null
)
