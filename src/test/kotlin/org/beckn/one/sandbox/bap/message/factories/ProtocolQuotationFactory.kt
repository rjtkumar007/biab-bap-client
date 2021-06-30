package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Quotation
import org.beckn.one.sandbox.bap.message.entities.QuotationBreakup
import org.beckn.one.sandbox.bap.schemas.ProtocolQuotation
import org.beckn.one.sandbox.bap.schemas.ProtocolQuotationBreakup

object ProtocolQuotationFactory {

  fun quoteForItems(itemIds: List<String>) = ProtocolQuotation(
    price = ProtocolPriceFactory.create(),
    breakup = itemIds.map { ProtocolQuotationBreakupFactory.forItem(it) },
    ttl = "30"
  )

  fun createAsEntity(protocol: ProtocolQuotation?) = protocol?.let {
    Quotation(
      price = ProtocolPriceFactory.createAsEntity(protocol.price),
      breakup = protocol.breakup?.mapNotNull { ProtocolQuotationBreakupFactory.createAsEntity(it) },
      ttl = protocol.ttl
    )
  }
}

object ProtocolQuotationBreakupFactory {

  private fun create(id: String, type: ProtocolQuotationBreakup.Type) = ProtocolQuotationBreakup(
    type = type,
    refId = id,
    price = ProtocolPriceFactory.create()
  )

  fun forItem(itemId: String) = create(itemId, ProtocolQuotationBreakup.Type.ITEM)
  fun forAddon(itemId: String) = create(itemId, ProtocolQuotationBreakup.Type.ADDON)
  fun forFulfilment(itemId: String) = create(itemId, ProtocolQuotationBreakup.Type.FULFILLMENT)
  fun forOffer(itemId: String) = create(itemId, ProtocolQuotationBreakup.Type.OFFER)

  fun createAsEntity(protocol: ProtocolQuotationBreakup?) = protocol?.let {
    QuotationBreakup(
      type = QuotationBreakup.Type.values().first { it.value == protocol.type?.value },
      refId = protocol.refId,
      price = ProtocolPriceFactory.createAsEntity(protocol.price)
    )
  }
}