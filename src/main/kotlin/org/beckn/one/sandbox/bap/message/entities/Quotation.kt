package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class Quotation @Default constructor(
  val price: Price? = null,
  val breakup: List<QuotationBreakup>? = null,
  val ttl: String? = null
)


data class QuotationBreakup @Default constructor(
  val type: Type? = null,
  val refId: String? = null,
  val title: String? = null,
  val price: Price? = null
) {

  enum class Type(val value: String) {
    ITEM("item"),
    OFFER("offer"),
    ADDON("add-on"),
    FULFILLMENT("fulfillment");
  }
}