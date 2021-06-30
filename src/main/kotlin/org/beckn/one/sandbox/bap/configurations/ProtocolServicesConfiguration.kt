package org.beckn.one.sandbox.bap.configurations

import org.beckn.one.sandbox.bap.message.entities.OnInit
import org.beckn.one.sandbox.bap.message.entities.OnSearch
import org.beckn.one.sandbox.bap.message.entities.OnSelect
import org.beckn.one.sandbox.bap.message.mappers.GenericResponseMapper
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.message.services.ResponseStorageServiceImpl
import org.beckn.one.sandbox.bap.schemas.ProtocolOnInit
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSearch
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSelect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProtocolServicesConfiguration {

  @Bean
  fun onSearchStorageService(
    @Autowired responseRepo: BecknResponseRepository<OnSearch>,
    @Autowired  mapper: GenericResponseMapper<ProtocolOnSearch, OnSearch>
  ): ResponseStorageService<ProtocolOnSearch> = ResponseStorageServiceImpl(responseRepo, mapper)

  @Bean
  fun onSelectStorageService(
    @Autowired responseRepo: BecknResponseRepository<OnSelect>,
    @Autowired  mapper: GenericResponseMapper<ProtocolOnSelect, OnSelect>
  ): ResponseStorageService<ProtocolOnSelect> = ResponseStorageServiceImpl(responseRepo, mapper)


  @Bean
  fun onInitStorageService(
    @Autowired responseRepo: BecknResponseRepository<OnInit>,
    @Autowired  mapper: GenericResponseMapper<ProtocolOnInit, OnInit>
  ): ResponseStorageService<ProtocolOnInit> = ResponseStorageServiceImpl(responseRepo, mapper)

}