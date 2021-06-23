package org.beckn.one.sandbox.bap.schemas

import com.fasterxml.jackson.annotation.JsonProperty
import org.beckn.one.sandbox.bap.Default

data class ProviderWithItems(
  val provider: Provider,
  val items: List<ProtocolItem>,
  val categories: List<ProtocolCategory>,
  val offers: List<ProtocolOffer>,
)

data class Provider(
  val id: String,
  val name: String,
  val descriptor: String?,
  val locations: List<String>,
  val images: List<String>?
)

data class ProtocolCategory @Default constructor(
  val id: String,
  val name: String,
  val descriptor: ProtocolDescriptor,
  val tags: Map<String, String>? = null
)

data class ProtocolOffer(
  val id: String,
  val name: String,
  val descriptor: String,
  @JsonProperty("category_ids") val categoryIds: List<String>,
  @JsonProperty("item_ids") val itemIds: List<String>
)

data class ProtocolItem @Default constructor(
  val id: String? = null,
  val name: String? = null,
  @JsonProperty("parent_item_id") val parentItemId: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val price: ProtocolPrice? = null,
  @JsonProperty("category_id") val categoryId: String? = null,
  val images: List<String>? = null,
  val tags: Map<String, String>? = null,
  val attributes: Map<String, String>? = null,
  val addons: List<ProtocolAddOn>? = null,
  @JsonProperty("configurable_attributes") val configurableAttributes: List<ProtocolConfigurableItemAttribute>? = null
)

data class ProtocolAddOn @Default constructor(
  val id: String,
  val descriptor: ProtocolDescriptor,
  val price: ProtocolPrice
)

data class ProtocolPrice @Default constructor(
  @JsonProperty("estimated_value") val estimated: String,
  @JsonProperty("computed_value") val computed: String,
  @JsonProperty("listed_value") val listed: String,
  @JsonProperty("offered_value") val offered: String,
  @JsonProperty("minimum_value") val minimum: String,
  @JsonProperty("maximum_value") val maximum: String
)

data class ProtocolConfigurableItemAttribute @Default constructor(
  val name: String,
  val permissibleValues: List<String>
)

