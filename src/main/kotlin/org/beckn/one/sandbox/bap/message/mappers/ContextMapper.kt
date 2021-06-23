package org.beckn.one.sandbox.bap.message.mappers

import org.beckn.one.sandbox.bap.message.entities.Context
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
interface ContextMapper {
  fun toSchema(entity: Context): org.beckn.one.sandbox.bap.schemas.Context
  fun fromSchema(schema: org.beckn.one.sandbox.bap.schemas.Context): Context
}