package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Catalog
import org.beckn.one.sandbox.bap.message.entities.Price
import org.beckn.one.sandbox.bap.message.entities.ProviderCatalog
import org.beckn.one.sandbox.bap.schemas.ProtocolCatalog
import org.beckn.one.sandbox.bap.schemas.ProtocolPrice
import org.beckn.one.sandbox.bap.schemas.ProtocolProviderCatalog

object ProtocolCatalogFactory {
  fun create(index: Int = 1): ProtocolCatalog {
    return ProtocolCatalog(
      bppProviders = listOf(
        ProtocolProviderCatalog(
          id = IdFactory.forProvider(index),
          descriptor = ProtocolDescriptorFactory.create("Retail-provider", IdFactory.forProvider(index)),
          categories = IdFactory.forCategory(IdFactory.forProvider(index), 1).map { ProtocolCategoryFactory.create(it) },
          items = IdFactory.forItems(IdFactory.forProvider(index), 1).map { ProtocolItemFactory.create(it) }
        )
      )
    )
  }

  fun createAsEntity(protocol: ProtocolCatalog?) = protocol?.let {
    Catalog(
      bppProviders = it.bppProviders?.map { bpp ->
        ProviderCatalog(
          id = bpp.id,
          descriptor = ProtocolDescriptorFactory.createAsEntity(bpp.descriptor),
          categories = bpp.categories?.mapNotNull { c -> ProtocolCategoryFactory.createAsEntity(c) },
          items = bpp.items?.map { i -> ProtocolItemFactory.createAsEntity(i) }
        )
      }
    )
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