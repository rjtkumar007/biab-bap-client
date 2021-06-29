package org.beckn.one.sandbox.bap.configurations

import org.beckn.one.sandbox.bap.message.entities.OnSelect
import org.beckn.one.sandbox.bap.message.entities.SearchResponse
import org.beckn.one.sandbox.bap.message.mappers.GenericResponseMapper
import org.beckn.one.sandbox.bap.message.repositories.BecknResponseRepository
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.message.services.ResponseStorageServiceImpl
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSelect
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProtocolServicesConfiguration {

  @Bean
  fun onSearchStorageService(
    @Autowired responseRepo: BecknResponseRepository<SearchResponse>,
    @Autowired  mapper: GenericResponseMapper<ProtocolSearchResponse, SearchResponse>
  ): ResponseStorageService<ProtocolSearchResponse> = ResponseStorageServiceImpl(responseRepo, mapper)

  @Bean
  fun onSelectStorageService(
    @Autowired responseRepo: BecknResponseRepository<OnSelect>,
    @Autowired  mapper: GenericResponseMapper<ProtocolOnSelect, OnSelect>
  ): ResponseStorageService<ProtocolOnSelect> = ResponseStorageServiceImpl(responseRepo, mapper)

}