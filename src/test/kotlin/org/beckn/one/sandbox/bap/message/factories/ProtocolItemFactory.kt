package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Item
import org.beckn.one.sandbox.bap.schemas.ProtocolItem

object ProtocolItemFactory {

  fun create(itemId: String) = ProtocolItem(
    id = "Item_$itemId",
    descriptor = ProtocolDescriptorFactory.create("provider-$itemId-item", itemId),
    price = ProtocolPriceFactory.create(),
    categoryId = "provider-$itemId-category-$itemId",
    tags = mapOf("item-tag1" to "item-value1"),
    matched = true,
    related = true,
    recommended = true
  )

  fun createAsEntity(protocol: ProtocolItem) = Item(
    id = protocol.id,
    descriptor = ProtocolDescriptorFactory.createAsEntity(protocol.descriptor),
    price = ProtocolPriceFactory.createAsEntity(protocol.price),
    categoryId = protocol.categoryId,
    tags = protocol.tags,
    matched = protocol.matched,
    related = protocol.related,
    recommended = protocol.recommended
  )
}