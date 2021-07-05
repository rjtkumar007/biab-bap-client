package org.beckn.one.sandbox.bap.message.mappers

import org.beckn.one.sandbox.bap.message.entities.*
import org.beckn.one.sandbox.bap.schemas.*
import org.mapstruct.InjectionStrategy
import org.mapstruct.Mapper
import org.mapstruct.ReportingPolicy

interface GenericResponseMapper<Protocol: ProtocolResponse, Entity: BecknResponseDao> {
  fun entityToProtocol(entity: Entity): Protocol
  fun protocolToEntity(schema: Protocol): Entity
}

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface OnSearchResponseMapper : GenericResponseMapper<ProtocolOnSearch, OnSearchDao> {
  override fun entityToProtocol(entity: OnSearchDao): ProtocolOnSearch
  override fun protocolToEntity(schema: ProtocolOnSearch): OnSearchDao
}


@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface OnSelectResponseMapper : GenericResponseMapper<ProtocolOnSelect, OnSelectDao> {
  override fun entityToProtocol(entity: OnSelectDao): ProtocolOnSelect
  override fun protocolToEntity(schema: ProtocolOnSelect): OnSelectDao
}

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface OnInitResponseMapper : GenericResponseMapper<ProtocolOnInit, OnInitDao> {
  override fun entityToProtocol(entity: OnInitDao): ProtocolOnInit
  override fun protocolToEntity(schema: ProtocolOnInit): OnInitDao
}

@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.WARN,
  injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
interface OnConfirmResponseMapper : GenericResponseMapper<ProtocolOnConfirm, OnConfirm> {
  override fun entityToProtocol(entity: OnConfirm): ProtocolOnConfirm
  override fun protocolToEntity(schema: ProtocolOnConfirm): OnConfirm
}