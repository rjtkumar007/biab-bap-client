package org.beckn.one.sandbox.bap.message.mappers

import org.beckn.one.sandbox.bap.message.entities.CatalogDao
import org.beckn.one.sandbox.bap.schemas.ProtocolCatalog
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface CatalogMapper {
  fun entityToSchema(source: CatalogDao): ProtocolCatalog
  fun schemaToEntity(source: ProtocolCatalog): CatalogDao
}