package org.beckn.one.sandbox.bap.schemas

import com.fasterxml.jackson.annotation.JsonProperty
import org.beckn.one.sandbox.bap.Default

data class ProviderWithItems(
  val provider: Provider,
  val items: List<Item>,
  val categories: List<Category>,
  val offers: List<Offer>,
)

data class Provider(
  val id: String,
  val name: String,
  val descriptor: String?,
  val locations: List<String>,
  val images: List<String>?
)

data class Category @Default constructor(
  val id: String,
  val name: String,
  val descriptor: Descriptor,
  val tags: Map<String, String>? = null
)

data class Offer(
  val id: String,
  val name: String,
  val descriptor: String,
  @JsonProperty("category_ids") val categoryIds: List<String>,
  @JsonProperty("item_ids") val itemIds: List<String>
)

data class Item @Default constructor(
  val id: String? = null,
  val name: String? = null,
  @JsonProperty("parent_item_id") val parentItemId: String? = null,
  val descriptor: Descriptor? = null,
  val price: Price? = null,
  @JsonProperty("category_id") val categoryId: String? = null,
  val images: List<String>? = null,
  val tags: Map<String, String>? = null,
  val attributes: Map<String, String>? = null,
  val addons: List<AddOn>? = null,
  @JsonProperty("configurable_attributes") val configurableAttributes: List<ConfigurableItemAttribute>? = null
)

data class AddOn @Default constructor(
  val id: String,
  val descriptor: String,
  val price: Price
)

data class Price @Default constructor(
  @JsonProperty("estimated_value") val estimated: String,
  @JsonProperty("computed_value") val computed: String,
  @JsonProperty("listed_value") val listed: String,
  @JsonProperty("offered_value") val offered: String,
  @JsonProperty("minimum_value") val minimum: String,
  @JsonProperty("maximum_value") val maximum: String
)

data class ConfigurableItemAttribute @Default constructor(
  val name: String,
  val permissibleValues: List<String>
)

