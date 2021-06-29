package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Category
import org.beckn.one.sandbox.bap.message.entities.ProviderCatalog
import org.beckn.one.sandbox.bap.schemas.ProtocolCategory
import org.beckn.one.sandbox.bap.schemas.ProtocolProviderCatalog

object ProtocolProviderCatalogFactory {

  fun create(index: Int) = ProtocolProviderCatalog(
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

  fun createAsEntity(protocol: ProtocolProviderCatalog) = ProviderCatalog(
    id = protocol.id,
    descriptor = ProtocolDescriptorFactory.createAsEntity(protocol.descriptor),
    categories = protocol.categories?.mapNotNull { ProtocolCategoryFactory.createAsEntity(it) },
    items = protocol.items?.map { ProtocolItemFactory.createAsEntity(it) },
  )
}

object ProtocolCategoryFactory{
  fun create(index: Int) = ProtocolCategory(
    id = "provider-$index-category-$index",
    descriptor = ProtocolDescriptorFactory.create("provider-$index-category", index),
    tags = mapOf("category-tag1" to "category-value1")
  )

  fun createAsEntity(protocol: ProtocolCategory?) = protocol?.let {
    Category(
      id = protocol.id,
      descriptor = ProtocolDescriptorFactory.createAsEntity(protocol.descriptor),
      tags = protocol.tags
    )
  }
}