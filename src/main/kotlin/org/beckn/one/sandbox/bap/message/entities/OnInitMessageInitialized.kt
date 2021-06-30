package org.beckn.one.sandbox.bap.schemas

data class ProtocolOnInitMessageInitialized (
  val provider: ProtocolOnInitMessageInitializedProvider? = null,
  val providerLocation: ProtocolOnInitMessageInitializedProviderLocation? = null,
  val items: List<ProtocolOnInitMessageInitializedItems>? = null,
  val addOns: List<ProtocolOnInitMessageInitializedAddOns>? = null,
  val offers: List<ProtocolOnInitMessageInitializedOffers>? = null,
  val billing: ProtocolBilling? = null,
  val fulfillment: ProtocolFulfillment? = null,
  val quote: ProtocolQuotation? = null,
  val payment: ProtocolPayment? = null
)

data class ProtocolOnInitMessageInitializedProviderLocation (
  val id: String? = null
)

data class ProtocolOnInitMessageInitializedProvider (
  val id: String? = null
)

data class ProtocolOnInitMessageInitializedItems (
  val id: String? = null,
  val quantity: ProtocolItemQuantityAllocated? = null
)
// TODO: Example of inline declaration
data class ProtocolItemQuantityAllocated (
  val count: Int? = null,
  val measure: ProtocolScalar? = null
)

data class ProtocolOnInitMessageInitializedAddOns (
  val id: String? = null
)

data class ProtocolOnInitMessageInitializedOffers (
  val id: String? = null
)

