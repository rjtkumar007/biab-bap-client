package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.schemas.*

class CatalogFactory {
  fun create(index: Int = 1): ProtocolCatalog {
    return ProtocolCatalog(
      bppProviders = listOf(
        ProtocolProviderCatalog(
          id = "provider-$index",
          descriptor = descriptor("Retail-provider", index),
          categories = listOf(
            ProtocolCategory(
              id = "provider-$index-category-$index",
              name = "provider-$index-category-$index name",
              descriptor = descriptor("provider-$index-category", index),
              tags = mapOf("category-tag1" to "category-value1")
            )
          ),
          items = listOf(
            ProtocolItem(
              id = "Item_$index",
              name = "Item_$index name",
              descriptor = descriptor("provider-$index-item", index),
              price = ProtocolPrice(
                minimum = "100",
                estimated = "101",
                computed = "102",
                offered = "103",
                listed = "104",
                maximum = "105",
              ),
              categoryId = "provider-$index-category-$index",
              images = listOf("https://image1.com", "https://image2.com"),
              tags = mapOf("item-tag1" to "item-value1"),
              attributes = mapOf("item-attribute1" to "item-attribute-value1"),
              addons = listOf(
                ProtocolAddOn(
                  id = "Addon_$index",
                  descriptor = descriptor("provider-$index-addon", index),
                  price = ProtocolPrice(
                    minimum = "50",
                    estimated = "51",
                    computed = "52",
                    offered = "53",
                    listed = "54",
                    maximum = "55",
                  )
                )
              ),
              configurableAttributes = listOf(
                ProtocolConfigurableItemAttribute(
                  name = "configurable-item-attribute-$index",
                  permissibleValues = listOf("configurable-item-attribute-$index-value1")
                )
              )
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
  )
}