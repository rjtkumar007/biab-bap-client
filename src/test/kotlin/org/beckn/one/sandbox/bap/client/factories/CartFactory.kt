package org.beckn.one.sandbox.bap.client.factories

import org.beckn.one.sandbox.bap.client.dtos.*
import org.beckn.one.sandbox.bap.schemas.ProtocolDescriptor
import org.beckn.one.sandbox.bap.schemas.ProtocolPrice
import org.beckn.one.sandbox.bap.schemas.ProtocolScalar
import java.math.BigDecimal
import java.util.*

class CartFactory {
  companion object {
    fun create(id: String? = getUuid(), transactionId: String = getUuid()) = CartDto(
      id = id,
      transactionId = transactionId,
      items = listOf(
        CartItemDto(
          descriptor = ProtocolDescriptor(
            name = "Cothas Coffee 1 kg",
            images = listOf("https://i.ibb.co/rZqPDd2/Coffee-2-Cothas.jpg"),
          ),
          price = ProtocolPrice(
            currency = "INR",
            value = "500"
          ),
          id = "cothas-coffee-1",
          bppId = "paisool",
          bppUri = "www.paisool.test",
          provider = CartItemProviderDto(
            id = "venugopala stores",
            providerLocations = listOf("13.001581,77.5703686")
          ),
          quantity = 1,
          measure = ProtocolScalar(
            value = BigDecimal.valueOf(1),
            unit = "kg"
          ),
        ),
        CartItemDto(
          descriptor = ProtocolDescriptor(
            name = "Malgudi Coffee 500 gm",
            images = listOf("https://i.ibb.co/wgXx7K6/Coffee-1-Malgudi.jpg"),
          ),
          price = ProtocolPrice(
            currency = "INR",
            value = "240"
          ),
          id = "malgudi-coffee-500-gm",
          bppId = "paisool",
          bppUri = "www.paisool.test",
          provider = CartItemProviderDto(
            id = "venugopala stores",
            providerLocations = listOf("13.001581,77.5703686")
          ),
          quantity = 1,
          measure = ProtocolScalar(
            value = BigDecimal.valueOf(500),
            unit = "gm"
          ),
        ),
      )
    )

    private fun getUuid() = UUID.randomUUID().toString()

    fun createV0(id: String? = null) = CartDtoV0(
      id = id, items = listOf(
        CartItemDtoV0(
          bppId = "paisool",
          provider = CartItemProviderDtoV0(
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
        CartItemDtoV0(
          bppId = "paisool",
          provider = CartItemProviderDtoV0(
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

    fun createWithMultipleBpp(id: String? = null) = CartDtoV0(
      id = id, items = listOf(
        CartItemDtoV0(
          bppId = "paisool",
          provider = CartItemProviderDtoV0(
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
        CartItemDtoV0(
          bppId = "shopx",
          provider = CartItemProviderDtoV0(
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