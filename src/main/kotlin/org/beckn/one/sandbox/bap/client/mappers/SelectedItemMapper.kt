package org.beckn.one.sandbox.bap.client.mappers

import org.beckn.one.sandbox.bap.client.dtos.CartItemDto
import org.beckn.one.sandbox.bap.schemas.ProtocolSelectedItem
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface SelectedItemMapper {
  fun dtoToProtocol(dto: CartItemDto): ProtocolSelectedItem
}