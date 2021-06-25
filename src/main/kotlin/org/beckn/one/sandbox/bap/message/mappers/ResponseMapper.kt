package org.beckn.one.sandbox.bap.message.mappers

import org.beckn.one.sandbox.bap.message.entities.BecknResponse
import org.beckn.one.sandbox.bap.message.entities.SearchResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

interface GenericResponseMapper<ProtoResp: ProtocolResponse, EntityResp: BecknResponse> {
  fun entityToProtocol(entity: EntityResp): ProtoResp
  fun protocolToEntity(schema: ProtoResp): EntityResp
}

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface SearchResponseMapper : GenericResponseMapper<ProtocolSearchResponse, SearchResponse> {
  override fun entityToProtocol(entity: SearchResponse): ProtocolSearchResponse
  override fun protocolToEntity(schema: ProtocolSearchResponse): SearchResponse
}