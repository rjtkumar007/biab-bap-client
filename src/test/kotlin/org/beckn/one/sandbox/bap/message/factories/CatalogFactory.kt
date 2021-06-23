package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.schemas.BecknCatalog
import org.beckn.one.sandbox.bap.schemas.Item
import org.beckn.one.sandbox.bap.schemas.ProviderCatalog

class CatalogFactory {
  fun create(index: Int = 1): BecknCatalog {
    return BecknCatalog(
      bppProviders = listOf(
        ProviderCatalog(
          id = "provider-$index.com",
          items = listOf(Item(id = "Item_$index"))
        )
      )
    )
  }
}