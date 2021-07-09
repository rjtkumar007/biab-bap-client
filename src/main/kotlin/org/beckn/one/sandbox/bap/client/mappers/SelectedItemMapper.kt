package org.beckn.one.sandbox.bap.client.mappers

import org.beckn.one.sandbox.bap.client.dtos.CartItemDto
import org.beckn.one.sandbox.bap.client.dtos.CartSelectedItemQuantity
import org.beckn.one.sandbox.bap.schemas.ProtocolItemQuantity
import org.beckn.one.sandbox.bap.schemas.ProtocolItemQuantityAllocated
import org.beckn.one.sandbox.bap.schemas.ProtocolSelectedItem
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.ReportingPolicy
import org.springframework.stereotype.Component

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR,
  uses = [SelectedQuantityMapper::class]
)
interface SelectedItemMapper {
  @Mapping(target = "quantity", source = "quantity")
  fun dtoToProtocol(dto: CartItemDto): ProtocolSelectedItem
}

@Component
class SelectedQuantityMapper {
  fun fromQuantity(quantity: CartSelectedItemQuantity) = ProtocolItemQuantity(
    selected = ProtocolItemQuantityAllocated(count = quantity.count, measure = quantity.measure)
  )
}