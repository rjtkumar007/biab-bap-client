package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class OnSelectMessageSelected @Default constructor(
  val provider: Provider? = null,
  val providerLocation: Location? = null,
  val items: List<Item>? = null,
  val addOns: List<AddOn>? = null,
  val offers: List<Offer>? = null,
  val quote: Quotation? = null
)
