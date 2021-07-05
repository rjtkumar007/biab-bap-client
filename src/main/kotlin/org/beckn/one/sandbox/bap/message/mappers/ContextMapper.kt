package org.beckn.one.sandbox.bap.message.mappers

import org.beckn.one.sandbox.bap.message.entities.ContextDao
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.WARN)
interface ContextMapper {
  fun toSchema(entity: ContextDao): org.beckn.one.sandbox.bap.schemas.ProtocolContext
  fun fromSchema(schema: org.beckn.one.sandbox.bap.schemas.ProtocolContext): ContextDao
}