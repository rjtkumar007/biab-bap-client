package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class OnSelectMessageSelectedDao @Default constructor(
  val provider: ProviderDao? = null,
  val providerLocation: LocationDao? = null,
  val items: List<ItemDao>? = null,
  val addOns: List<AddOnDao>? = null,
  val offers: List<OfferDao>? = null,
  val quote: QuotationDao? = null
)
