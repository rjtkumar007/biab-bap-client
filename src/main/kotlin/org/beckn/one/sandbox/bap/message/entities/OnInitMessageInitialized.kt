package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class OnInitMessageInitialized @Default constructor(
  val provider: OnInitMessageInitializedProvider? = null,
  val providerLocation: OnInitMessageInitializedProviderLocation? = null,
  val items: List<OnInitMessageInitializedItems>? = null,
  val addOns: List<OnInitMessageInitializedAddOns>? = null,
  val offers: List<OnInitMessageInitializedOffers>? = null,
  val billing: Billing? = null,
  val fulfillment: Fulfillment? = null,
  val quote: Quotation? = null,
  val payment: Payment? = null
)

data class OnInitMessageInitializedProviderLocation @Default constructor(
  val id: String? = null
)

data class OnInitMessageInitializedProvider @Default constructor(
  val id: String? = null
)

data class OnInitMessageInitializedItems @Default constructor(
  val id: String? = null,
  val quantity: ItemQuantityAllocated? = null
)

// TODO: Example of inline declaration
data class ItemQuantityAllocated @Default constructor(
  val count: Int? = null,
  val measure: Scalar? = null
)

data class OnInitMessageInitializedAddOns @Default constructor(
  val id: String? = null
)

data class OnInitMessageInitializedOffers @Default constructor(
  val id: String? = null
)

