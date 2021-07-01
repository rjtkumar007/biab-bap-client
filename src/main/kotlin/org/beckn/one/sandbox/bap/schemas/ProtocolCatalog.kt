package org.beckn.one.sandbox.bap.schemas

import com.fasterxml.jackson.annotation.JsonProperty
import org.beckn.one.sandbox.bap.Default
import java.time.LocalDateTime

data class ProtocolCatalog @Default constructor(
  @JsonProperty("bpp/descriptor") val bppDescriptor: ProtocolDescriptor? = null,
  @JsonProperty("bpp/providers") val bppProviders: List<ProtocolProviderCatalog>? = null,
  @JsonProperty("bpp/categories") val bppCategories: List<ProtocolCategory>? = null,
  val exp: LocalDateTime? = null
)

data class ProtocolProviderCatalog @Default constructor(
  val id: String? = null,
  val descriptor: ProtocolDescriptor? = null,
  val locations: List<ProtocolLocation>? = null,
  val categories: List<ProtocolCategory>? = null,
  val items: List<ProtocolItem>? = null,
  val tags: Map<String, String>? = null,
  val exp: LocalDateTime? = null
)