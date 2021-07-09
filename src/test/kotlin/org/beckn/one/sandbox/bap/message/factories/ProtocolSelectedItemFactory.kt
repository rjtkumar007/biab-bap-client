package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.*
import org.beckn.one.sandbox.bap.schemas.ProtocolItemQuantity
import org.beckn.one.sandbox.bap.schemas.ProtocolItemQuantityAllocated
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import org.beckn.one.sandbox.bap.schemas.ProtocolSelectedItem
import java.math.BigDecimal

object ProtocolSelectedItemFactory {
  fun create(itemId: String) = ProtocolSelectedItem(
    id = "Item_$itemId",
    quantity = ProtocolItemQuantity(
      selected = ProtocolItemQuantityAllocated(count = 1, measure = ProtocolScalar(BigDecimal.valueOf(100), "INR"))
    )
  )

  fun createAsEntity(protocol: ProtocolSelectedItem) = SelectedItemDao(
    id = protocol.id,
    descriptor = ProtocolDescriptorFactory.createAsEntity(protocol.descriptor),
    price = ProtocolPriceFactory.createAsEntity(protocol.price),
    categoryId = protocol.categoryId,
    tags = protocol.tags,
    time = ProtocolTimeFactory.timeAsEntity(protocol.time),
    quantity = ItemQuantityDao(
      selected = protocol.quantity.selected?.let {
        ItemQuantityAllocatedDao(
          count = it.count,
          measure = it.measure?.let { m ->
            ScalarDao(
              value = m.value,
              unit = m.unit,
              type = ScalarDao.Type.values().find { s -> s.value == m.type?.value },
              estimatedValue = m.estimatedValue,
              computedValue = m.computedValue,
              range = m.range?.let { s -> ScalarRangeDao(min = s.min, max = s.max) }
            )
          }
        )
      }
    )
  )
}