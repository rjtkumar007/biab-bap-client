package org.beckn.one.sandbox.bap.client.factories

import org.beckn.one.sandbox.bap.client.dtos.*
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import org.beckn.one.sandbox.bap.schemas.factories.UuidFactory
import java.math.BigDecimal

class CartFactory {
  companion object {
    fun create(
      id: String? = UuidFactory().create(),
      transactionId: String = UuidFactory().create(),
      bpp1Uri: String = "www.local-coffee-house.in",
      bpp2Uri: String = bpp1Uri,
      provider1Id: String = "venugopala stores",
      provider1Location: List<String> = listOf("venugopala stores location 1"),
      provider2Id: String = provider1Id,
      provider2Location: List<String> = provider1Location,
      items: List<CartItemDto> = listOf(
        CartItemFactory.cothasCoffee(
          bppId = bpp1Uri,
          providerId = provider1Id,
          providerLocation = provider1Location
        ),
        CartItemFactory.malgudiCoffee(
          bppId = bpp2Uri,
          providerId = provider2Id,
          providerLocation = provider2Location
        ),
      )
    ) = CartDto(
      id = id,
      transactionId = transactionId,
      items = items
    )

    fun createV0(id: String? = null) = CartDtoV0(
      id = id, items = listOf(
        CartItemDtoV0(
          bppId = "www.local-coffee-house.in",
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
          bppId = "www.local-coffee-house.in",
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
          bppId = "www.local-coffee-house.in",
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