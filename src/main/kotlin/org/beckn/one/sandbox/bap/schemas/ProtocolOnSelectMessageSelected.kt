package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

data class ProtocolOnSelectMessageSelected @Default constructor(
  val provider: ProtocolProvider? = null,
  val providerLocation: ProtocolLocation? = null,
  val items: List<ProtocolItem>? = null,
  val addOns: List<ProtocolAddOn>? = null,
  val offers: List<ProtocolOffer>? = null,
  val quote: ProtocolQuotation? = null
)
