package org.beckn.one.sandbox.bap.client.factories

import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.dtos.CartDtoV0
import org.beckn.one.sandbox.bap.client.dtos.CartItemDtoV0
import org.beckn.one.sandbox.bap.client.dtos.CartItemProviderDtoV0
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import java.math.BigDecimal
import java.util.*

class CartFactory {
  companion object {
    fun create(
      id: String? = UuidFactory.create(),
      transactionId: String = UuidFactory.create(),
      bppUri: String = "www.local-coffee-house.in"
    ) =
      CartDto(
        id = id,
        transactionId = transactionId,
        items = listOf(CartItemFactory.cothasCoffee(bppUri), CartItemFactory.malgudiCoffee(bppUri))
      )

    fun createV0(id: String? = null) = CartDtoV0(
      id = id, items = listOf(
        CartItemDtoV0(
          bppId = "local-coffee-house",
          provider = CartItemProviderDtoV0(
            id = "venugopala stores",
            locations = listOf("13.001581,77.5703686")
          ),
          itemId = "cothas-coffee-1",
          quantity = 2,
          measure = ProtocolScalar(
            value = BigDecimal.valueOf(500),
            unit = "gm"
          )
        ),
        CartItemDtoV0(
          bppId = "local-coffee-house",
          provider = CartItemProviderDtoV0(
            id = "maruthi-stores",
            locations = listOf("12.9995218,77.5704439")
          ),
          itemId = "malgudi-coffee-500-gms",
          quantity = 1,
          measure = ProtocolScalar(
            value = BigDecimal.valueOf(1),
            unit = "kg"
          )
        )
      )
    )

    fun createWithMultipleBpp(id: String? = null) = CartDtoV0(
      id = id, items = listOf(
        CartItemDtoV0(
          bppId = "local-coffee-house",
          provider = CartItemProviderDtoV0(
            id = "venugopala stores",
            locations = listOf("13.001581,77.5703686")
          ),
          itemId = "cothas-coffee-1",
          quantity = 2,
          measure = ProtocolScalar(
            value = BigDecimal.valueOf(500),
            unit = "gm"
          )
        ),
        CartItemDtoV0(
          bppId = "shopx",
          provider = CartItemProviderDtoV0(
            id = "maruthi-stores",
            locations = listOf("12.9995218,77.5704439")
          ),
          itemId = "malgudi-coffee-500-gms",
          quantity = 1,
          measure = ProtocolScalar(
            value = BigDecimal.valueOf(1),
            unit = "kg"
          )
        )
      )
    )
  }
}