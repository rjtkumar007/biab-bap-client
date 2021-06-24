package org.beckn.one.sandbox.bap.message.mappers

import org.beckn.one.sandbox.bap.message.entities.SearchResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface SearchResponseMapper {
  fun entityToSchema(entity: SearchResponse): org.beckn.one.sandbox.bap.schemas.SearchResponse
  fun schemaToEntity(schema: org.beckn.one.sandbox.bap.schemas.SearchResponse): SearchResponse
}