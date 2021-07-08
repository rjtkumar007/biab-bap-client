package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.SelectedItemDao
import org.beckn.one.sandbox.bap.message.entities.SelectedItemQuantityDao
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import org.beckn.one.sandbox.bap.schemas.ProtocolSelectedItem
import org.beckn.one.sandbox.bap.schemas.ProtocolSelectedItemQuantity
import java.math.BigDecimal

object ProtocolSelectedItemFactory {
  fun create(itemId: String) = ProtocolSelectedItem(
    id = "Item_$itemId",
    quantity = ProtocolSelectedItemQuantity(count = 1, measure = ProtocolScalar(BigDecimal.valueOf(100), "INR"))
  )

  fun createAsEntity(protocol: ProtocolSelectedItem) = SelectedItemDao(
    id = protocol.id,
    descriptor = ProtocolDescriptorFactory.createAsEntity(protocol.descriptor),
    price = ProtocolPriceFactory.createAsEntity(protocol.price),
    categoryId = protocol.categoryId,
    tags = protocol.tags,
    time = ProtocolTimeFactory.timeAsEntity(protocol.time),
    quantity = SelectedItemQuantityDao(count = protocol.quantity.count, measure = protocol.quantity.measure)
  )
}