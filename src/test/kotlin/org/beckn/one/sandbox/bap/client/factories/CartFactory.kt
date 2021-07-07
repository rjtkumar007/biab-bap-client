package org.beckn.one.sandbox.bap.client.factories

import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.dtos.CartItemDto
import org.beckn.one.sandbox.bap.client.dtos.CartItemProviderDto
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import java.math.BigDecimal

class CartFactory {
  companion object {
    fun create(id: String? = null) = CartDto(
      id = id, items = listOf(
        CartItemDto(
          bppId = "paisool",
          provider = CartItemProviderDto(
            id = "venugopala stores",
            providerLocations = listOf("13.001581,77.5703686")
          ),
          itemId = "cothas-coffee-1",
          quantity = 2,
          measure = ProtocolScalar(
            value = BigDecimal.valueOf(500),
            unit = "gm"
          )
        ),
        CartItemDto(
          bppId = "paisool",
          provider = CartItemProviderDto(
            id = "maruthi-stores",
            providerLocations = listOf("12.9995218,77.5704439")
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

    fun createWithMultipleBpp(id: String? = null) = CartDto(
      id = id, items = listOf(
        CartItemDto(
          bppId = "paisool",
          provider = CartItemProviderDto(
            id = "venugopala stores",
            providerLocations = listOf("13.001581,77.5703686")
          ),
          itemId = "cothas-coffee-1",
          quantity = 2,
          measure = ProtocolScalar(
            value = BigDecimal.valueOf(500),
            unit = "gm"
          )
        ),
        CartItemDto(
          bppId = "shopx",
          provider = CartItemProviderDto(
            id = "maruthi-stores",
            providerLocations = listOf("12.9995218,77.5704439")
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