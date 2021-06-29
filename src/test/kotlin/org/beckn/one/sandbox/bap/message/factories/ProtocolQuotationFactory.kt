package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Quotation
import org.beckn.one.sandbox.bap.message.entities.QuotationBreakup
import org.beckn.one.sandbox.bap.schemas.ProtocolQuotation
import org.beckn.one.sandbox.bap.schemas.ProtocolQuotationBreakup

object ProtocolQuotationFactory {

  fun quoteForItems(itemIds: List<Int>) = ProtocolQuotation(
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

  private fun create(id: Int, type: ProtocolQuotationBreakup.Type) = ProtocolQuotationBreakup(
    type = type,
    refId = "quote-breakup-$id",
    price = ProtocolPriceFactory.create()
  )

  fun forItem(id: Int) = create(id, ProtocolQuotationBreakup.Type.ITEM)
  fun forAddon(id: Int) = create(id, ProtocolQuotationBreakup.Type.ADDON)
  fun forFulfilment(id: Int) = create(id, ProtocolQuotationBreakup.Type.FULFILLMENT)
  fun forOffer(id: Int) = create(id, ProtocolQuotationBreakup.Type.OFFER)

  fun createAsEntity(protocol: ProtocolQuotationBreakup?) = protocol?.let {
    QuotationBreakup(
      type = QuotationBreakup.Type.values().first { it.value == protocol.type?.value },
      refId = protocol.refId,
      price = ProtocolPriceFactory.createAsEntity(protocol.price)
    )
  }
}