package org.beckn.one.sandbox.bap.client.factories

import org.beckn.one.sandbox.bap.client.dtos.CartItemDto
import org.beckn.one.sandbox.bap.client.dtos.CartItemProviderDto
import org.beckn.one.sandbox.bap.client.dtos.CartSelectedItemQuantity
import org.beckn.one.sandbox.bap.schemas.ProtocolDescriptor
import org.beckn.one.sandbox.bap.schemas.ProtocolPrice
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import java.math.BigDecimal

class CartItemFactory {
  companion object {
    fun cothasCoffee(
      bppId: String = "www.local-coffee-house.in",
      bppUri: String = bppId,
      providerId: String = "venugopala stores",
      providerLocation: List<String> = listOf("$providerId location 1"),
    ) = CartItemDto(
      descriptor = ProtocolDescriptor(
        name = "Cothas Coffee 1 kg",
        images = listOf("https://i.ibb.co/rZqPDd2/Coffee-2-Cothas.jpg"),
      ),
      price = ProtocolPrice(
        currency = "INR",
        value = "500"
      ),
      id = "cothas-coffee-1",
      bppId = bppId,
      bppUri = bppUri,
      provider = CartItemProviderDto(
        id = providerId,
        locations = providerLocation
      ),
      quantity = CartSelectedItemQuantity(
        count = 1,
        measure = ProtocolScalar(
          value = BigDecimal.valueOf(1),
          unit = "kg"
        )
      ),
    )

    fun malgudiCoffee(
      bppId: String = "www.local-coffee-house.in",
      bppUri: String = bppId,
      providerId: String = "venugopala stores",
      providerLocation: List<String> = listOf("$providerId location 1"),
    ) = CartItemDto(
      descriptor = ProtocolDescriptor(
        name = "Malgudi Coffee 500 gm",
        images = listOf("https://i.ibb.co/wgXx7K6/Coffee-1-Malgudi.jpg"),
      ),
      price = ProtocolPrice(
        currency = "INR",
        value = "240"
      ),
      id = "malgudi-coffee-500-gm",
      bppId = bppId,
      bppUri = bppUri,
      provider = CartItemProviderDto(
        id = providerId,
        locations = providerLocation
      ),
      quantity = CartSelectedItemQuantity(
        count = 1,
        measure = ProtocolScalar(
          value = BigDecimal.valueOf(500),
          unit = "gm"
        )
      ),
    )
  }
}