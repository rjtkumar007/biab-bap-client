package org.beckn.one.sandbox.bap.client.mappers

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.client.dtos.CartItemDto
import org.beckn.one.sandbox.bap.client.dtos.CartItemProviderDto
import org.beckn.one.sandbox.bap.client.dtos.CartSelectedItemQuantity
import org.beckn.one.sandbox.bap.schemas.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class SelectedItemMapperSpec @Autowired constructor(
  private val selectedItemMapper: SelectedItemMapper
) : DescribeSpec() {
  init {
    describe("Selected Item Mapper") {
      it("should map dto to protocol") {
        val dto = CartItemDto(
          id = "cothas-coffee-1",
          bppId = "www.local-coffee-house.in",
          provider = CartItemProviderDto(
            id = "venugopala stores",
            locations = listOf("13.001581,77.5703686")
          ),
          quantity = CartSelectedItemQuantity(
            count = 1,
            measure = ProtocolScalar(
              value = BigDecimal.valueOf(1),
              unit = "kg"
            )
          ),
        )

        val protocol = selectedItemMapper.dtoToProtocol(dto)

        protocol shouldBe ProtocolSelectedItem(
          id = dto.id,
          quantity =
          ProtocolItemQuantityAllocated(
            count = dto.quantity.count, measure = dto.quantity.measure
          )
        )
      }
    }
  }

}