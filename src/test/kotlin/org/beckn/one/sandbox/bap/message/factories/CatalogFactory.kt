package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.schemas.*

object CatalogFactory {
  fun create(index: Int = 1): ProtocolCatalog {
    return ProtocolCatalog(
      bppProviders = listOf(
        ProtocolProviderCatalog(
          id = "provider-$index",
          descriptor = descriptor("Retail-provider", index),
          categories = listOf(
            ProtocolCategory(
              id = "provider-$index-category-$index",
              descriptor = descriptor("provider-$index-category", index),
              tags = mapOf("category-tag1" to "category-value1")
            )
          ),
          items = listOf(
            ProtocolItem(
              id = "Item_$index",
              descriptor = descriptor("provider-$index-item", index),
              price = ProtocolPrice(
                currency = "Rupees",
                value = "99",
                minimumValue = "100",
                estimatedValue = "101",
                computedValue = "102",
                offeredValue = "103",
                listedValue = "104",
                maximumValue = "105",
              ),
              categoryId = "provider-$index-category-$index",
              tags = mapOf("item-tag1" to "item-value1"),
              matched = true,
              related = true,
              recommended = true
            )
          )
        )
      )
    )
  }

  private fun descriptor(type: String, index: Int) = ProtocolDescriptor(
    name = "$type-$index name",
    code = "$type-$index code",
    symbol = "$type-$index symbol",
    shortDesc = "A short description about $type-$index",
    longDesc = "A long description about $type-$index",
    images = listOf("uri:https://$type-$index-image-1.com", "uri:https://$type-$index-image-2.com"),
    audio = "$type-$index-image-audio-file-path",
    threeDRender = "$type-$index-3d"
  )
}