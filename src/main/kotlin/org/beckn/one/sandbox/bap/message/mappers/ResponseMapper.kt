package org.beckn.one.sandbox.bap.message.mappers

import org.beckn.one.sandbox.bap.message.entities.BecknResponse
import org.beckn.one.sandbox.bap.message.entities.OnInit
import org.beckn.one.sandbox.bap.message.entities.OnSearch
import org.beckn.one.sandbox.bap.message.entities.OnSelect
import org.beckn.one.sandbox.bap.schemas.ProtocolOnInit
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSearch
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSelect
import org.beckn.one.sandbox.bap.schemas.ProtocolResponse
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

interface GenericResponseMapper<Protocol: ProtocolResponse, Entity: BecknResponse> {
  fun entityToProtocol(entity: Entity): Protocol
  fun protocolToEntity(schema: Protocol): Entity
}

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface OnSearchResponseMapper : GenericResponseMapper<ProtocolOnSearch, OnSearch> {
  override fun entityToProtocol(entity: OnSearch): ProtocolOnSearch
  override fun protocolToEntity(schema: ProtocolOnSearch): OnSearch
}


@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface OnSelectResponseMapper : GenericResponseMapper<ProtocolOnSelect, OnSelect> {
  override fun entityToProtocol(entity: OnSelect): ProtocolOnSelect
  override fun protocolToEntity(schema: ProtocolOnSelect): OnSelect
}

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface OnInitResponseMapper : GenericResponseMapper<ProtocolOnInit, OnInit> {
  override fun entityToProtocol(entity: OnInit): ProtocolOnInit
  override fun protocolToEntity(schema: ProtocolOnInit): OnInit
}