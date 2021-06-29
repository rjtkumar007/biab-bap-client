package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Price
import org.beckn.one.sandbox.bap.schemas.ProtocolCatalog
import org.beckn.one.sandbox.bap.schemas.ProtocolCategory
import org.beckn.one.sandbox.bap.schemas.ProtocolPrice
import org.beckn.one.sandbox.bap.schemas.ProtocolProviderCatalog

object ProtocolCatalogFactory {
  fun create(index: Int = 1): ProtocolCatalog {
    return ProtocolCatalog(
      bppProviders = listOf(
        ProtocolProviderCatalog(
          id = "provider-$index",
          descriptor = ProtocolDescriptorFactory.create("Retail-provider", index),
          categories = listOf(
            ProtocolCategory(
              id = "provider-$index-category-$index",
              descriptor = ProtocolDescriptorFactory.create("provider-$index-category", index),
              tags = mapOf("category-tag1" to "category-value1")
            )
          ),
          items = listOf(ProtocolItemFactory.create(1))
        )
      )
    )
  }

  fun createAsEntity(protocol: ProtocolCatalog?) = protocol.let {

  }
}


object ProtocolPriceFactory {

  fun create() = ProtocolPrice(
    currency = "Rupees",
    value = "99",
    minimumValue = "100",
    estimatedValue = "101",
    computedValue = "102",
    offeredValue = "103",
    listedValue = "104",
    maximumValue = "105"
  )

  fun createAsEntity(protocol: ProtocolPrice?) = protocol?.let {
    Price(
      currency = protocol.currency,
      value = protocol.value,
      minimumValue = protocol.minimumValue,
      estimatedValue = protocol.estimatedValue,
      computedValue = protocol.computedValue,
      offeredValue = protocol.offeredValue,
      listedValue = protocol.listedValue,
      maximumValue = protocol.maximumValue
    )
  }

}