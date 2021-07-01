package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class Order @Default constructor(
  val provider: SelectMessageSelectedProvider,
  val items: List<SelectMessageSelectedItems>,
  val addOns: List<SelectMessageSelectedAddOns>,
  val offers: List<SelectMessageSelectedOffers>,
  val billing: Billing,
  val fulfillment: Fulfillment,
  val quote: Quotation,
  val payment: Payment,
  val id: String? = null,
  val state: String? = null,
  val createdAt: java.time.OffsetDateTime? = null,
  val updatedAt: java.time.OffsetDateTime? = null
)


data class SelectMessageSelectedProvider @Default constructor(
  val id: String,
  val locations: List<SelectMessageSelectedProviderLocations>
)

data class SelectMessageSelectedProviderLocations @Default constructor(
  val id: String
)

// TODO can be common
data class SelectMessageSelectedAddOns @Default constructor(
  val id: String
)

// TODO similar to OnInitMessageInitializedItems
data class SelectMessageSelectedItems @Default constructor(
  val id: String,
  val quantity: ItemQuantityAllocated
)

data class SelectMessageSelectedOffers @Default constructor(
  val id: String
)