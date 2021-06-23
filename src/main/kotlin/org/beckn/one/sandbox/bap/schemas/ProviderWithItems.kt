package org.beckn.one.sandbox.bap.schemas

import com.fasterxml.jackson.annotation.JsonProperty
import org.beckn.one.sandbox.bap.Default
import java.time.LocalDateTime

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
  val parentCategoryId: String? = null,
  val descriptor: ProtocolDescriptor,
  val time: LocalDateTime? = null,
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
  val parentItemId: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val price: ProtocolPrice? = null,
  val categoryId: String? = null,
  val locationId: String? = null,
  val time: LocalDateTime? = null,
  val matched: Boolean?,
  val related: Boolean?,
  val recommended: Boolean?,
  val tags: Map<String, String>? = null
)

data class ProtocolAddOn @Default constructor(
  val id: String,
  val descriptor: ProtocolDescriptor,
  val price: ProtocolPrice
)

data class ProtocolPrice @Default constructor(
  val currency: String,
  val value: String,
  val estimatedValue: String,
  val computedValue: String,
  val listedValue: String,
  val offeredValue: String,
  val minimumValue: String,
  val maximumValue: String
)

data class ProtocolConfigurableItemAttribute @Default constructor(
  val name: String,
  val permissibleValues: List<String>
)

