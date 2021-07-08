package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar

data class OnSelectMessageSelectedDao @Default constructor(
  val provider: ProviderDao? = null,
  val providerLocation: LocationDao? = null,
  val items: List<SelectedItemDao>? = null,
  val addOns: List<AddOnDao>? = null,
  val offers: List<OfferDao>? = null,
  val quote: QuotationDao? = null
)

data class SelectedItemDao @Default constructor(
  val id: String? = null,
  val parentItemId: String? = null,
  val descriptor: DescriptorDao? = null,
  val price: PriceDao? = null,
  val categoryId: String? = null,
  val locationId: String? = null,
  val time: TimeDao? = null,
  val tags: Map<String, String>? = null,
  val quantity: SelectedItemQuantityDao,
)

data class SelectedItemQuantityDao @Default constructor(
  val count: Int,
  val measure: ProtocolScalar?
)
