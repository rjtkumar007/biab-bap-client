package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

data class ProtocolOrder @Default constructor(
  val provider: ProtocolSelectMessageSelectedProvider,
  val items: List<ProtocolSelectMessageSelectedItems>,
  val addOns: List<ProtocolSelectMessageSelectedAddOns>,
  val offers: List<ProtocolSelectMessageSelectedOffers>,
  val billing: ProtocolBilling,
  val fulfillment: ProtocolFulfillment,
  val quote: ProtocolQuotation,
  val payment: ProtocolPayment,
  val id: String? = null,
  val state: String? = null,
  val createdAt: java.time.OffsetDateTime? = null,
  val updatedAt: java.time.OffsetDateTime? = null
)


data class ProtocolSelectMessageSelectedProvider @Default constructor(
  val id: String,
  val locations: List<ProtocolSelectMessageSelectedProviderLocations>
)

data class ProtocolSelectMessageSelectedProviderLocations @Default constructor(
  val id: String
)

// TODO can be common
data class ProtocolSelectMessageSelectedAddOns @Default constructor(
  val id: String
)

// TODO similar to OnInitMessageInitializedItems
data class ProtocolSelectMessageSelectedItems @Default constructor(
  val id: String,
  val quantity: ProtocolItemQuantityAllocated
)

data class ProtocolSelectMessageSelectedOffers @Default constructor(
  val id: String
)