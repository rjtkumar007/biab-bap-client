package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.OnSelectMessageSelected
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSelectMessageSelected

object ProtocolOnSelectMessageSelectedFactory {

  fun create(index: Int, numberOfItems: Int = 2): ProtocolOnSelectMessageSelected {
    val itemIds = generateSequence(
      seedFunction = { 0 },
      nextFunction = { (it + 1).takeIf { i -> i < numberOfItems } }
    ).toList()

    return ProtocolOnSelectMessageSelected(
      provider = ProtocolProviderFactory.create(index),
      providerLocation = null,
      items = itemIds.map { ProtocolItemFactory.create(it) },
      addOns = null,
      offers = null,
      quote = ProtocolQuotationFactory.quoteForItems(listOf(1, 2))
    )
  }

  fun createAsEntity(protocol: ProtocolOnSelectMessageSelected?) = protocol?.let {
    OnSelectMessageSelected(
      provider = ProtocolProviderFactory.createAsEntity(protocol.provider),
      providerLocation = null,
      items = protocol.items?.map { ProtocolItemFactory.createAsEntity(it) },
      addOns = null,
      offers = null,
      quote = ProtocolQuotationFactory.createAsEntity(protocol.quote)
    )
  }

}