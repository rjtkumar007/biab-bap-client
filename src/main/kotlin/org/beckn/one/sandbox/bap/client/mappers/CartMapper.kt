package org.beckn.one.sandbox.bap.client.mappers

import org.beckn.one.sandbox.bap.client.daos.CartDao
import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface CartMapper {
  fun dtoToDao(source: CartDto): CartDao
  fun daoToDto(dao: CartDao): CartDto
}