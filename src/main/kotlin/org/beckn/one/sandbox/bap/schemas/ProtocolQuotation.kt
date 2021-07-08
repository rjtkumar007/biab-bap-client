package org.beckn.one.sandbox.bap.schemas

import com.fasterxml.jackson.annotation.JsonProperty
import org.beckn.one.sandbox.bap.Default

data class ProtocolQuotation @Default constructor(
  val price: ProtocolPrice? = null,
  val breakup: List<ProtocolQuotationBreakup>? = null,
  val ttl: String? = null
)


data class ProtocolQuotationBreakup @Default constructor(
  val type: Type? = null,
  val refId: String? = null,
  val title: String? = null,
  val price: ProtocolPrice? = null
) {

  enum class Type(val value: String) {
    ITEM("item"),
    OFFER("offer"),
    @JsonProperty("add-on") ADDON("add-on"),
    FULFILLMENT("fulfillment");
  }
}