package org.beckn.one.sandbox.bap.client.mappers

import org.beckn.one.sandbox.bap.client.daos.CartDao
import org.beckn.one.sandbox.bap.client.dtos.CartDtoV0
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface CartMapper {
  fun dtoToDao(source: CartDtoV0): CartDao
  fun daoToDto(dao: CartDao): CartDtoV0
}