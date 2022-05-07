package org.beckn.one.sandbox.bap.message.factories

import org.beckn.protocol.schemas.OndcStatutoryPackagedCommodities
import org.beckn.protocol.schemas.OndcStatutoryPackagedFood
import org.beckn.protocol.schemas.ProtocolItem

object ProtocolItemFactory {

  fun create(itemId: String) = ProtocolItem(
    id = "Item_$itemId",
    descriptor = ProtocolDescriptorFactory.create("provider-$itemId-item", itemId),
    price = ProtocolPriceFactory.create(),
    categoryId = "provider-$itemId-category-$itemId",
    tags = mapOf("item-tag1" to "item-value1"),
    matched = true,
    related = true,
    recommended = true,
    time = ProtocolTimeFactory.fixedRange("range"),
    ondcReturnable = true,
    ondcCancellable = true,
    ondcSellerPickupReturn = true,
    ondcTimeToShip = "10:00",
    ondcAvailableOnCod = true,
    ondcStatutoryPackagedCommodities = OndcStatutoryPackagedCommodities(),
    ondcStatutoryPackagedFood = OndcStatutoryPackagedFood(),
    ondcReturnWindow = "true",
  )
}