package org.beckn.one.sandbox.bap.client.mappers

import org.beckn.one.sandbox.bap.client.dtos.ClientCatalog
import org.beckn.one.sandbox.bap.schemas.ProtocolCatalog
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface ClientCatalogMapper {
  fun protocolToClientDto(source: ProtocolCatalog): ClientCatalog
}