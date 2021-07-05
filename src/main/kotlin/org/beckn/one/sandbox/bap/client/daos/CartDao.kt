package org.beckn.one.sandbox.bap.client.daos

import org.beckn.one.sandbox.bap.Default
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import org.litote.kmongo.newId


data class CartDao @Default constructor(
  @field:BsonId val _id: Id<String> = newId(),
  val id: String? = null,
  val items: List<CartItemDao>? = null
) {
  companion object {
    const val collectionName = "carts"
  }
}

data class CartItemDao @Default constructor(
  val bppId: String,
  val provider: CartItemProviderDao,
  val itemId: String,
  val quantity: Int,
  val measure: ScalarDao? = null
)

data class CartItemProviderDao @Default constructor(
  val id: String,
  val providerLocations: List<String>? = null
)

