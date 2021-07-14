package org.beckn.one.sandbox.bap.configurations

import org.beckn.one.sandbox.bap.client.dtos.ClientInitResponse
import org.beckn.one.sandbox.bap.client.dtos.ClientSearchResponse
import org.beckn.one.sandbox.bap.client.mappers.ClientCatalogMapper
import org.beckn.one.sandbox.bap.client.services.GenericOnPollService
import org.beckn.one.sandbox.bap.client.services.GenericOnPollTransformer
import org.beckn.one.sandbox.bap.client.services.InitClientResponseMapper
import org.beckn.one.sandbox.bap.client.services.SearchClientSearchResponseMapper
import org.beckn.one.sandbox.bap.message.services.MessageService
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.schemas.ProtocolOnInit
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSearch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClientServicesConfiguration @Autowired constructor(
  private val clientCatalogMapper: ClientCatalogMapper
) {


  @Bean
  fun forSearchResults(): GenericOnPollTransformer<ProtocolOnSearch, ClientSearchResponse> =
    SearchClientSearchResponseMapper(clientCatalogMapper)

  @Bean
  @Qualifier("InitTransformer")
  fun forInitResults(): GenericOnPollTransformer<ProtocolOnInit, ClientInitResponse> =
    InitClientResponseMapper()

  @Bean
  fun searchResultReplyService(
    @Autowired messageService: MessageService,
    @Autowired responseStorageService: ResponseStorageService<ProtocolOnSearch>,
    @Autowired transformer: GenericOnPollTransformer<ProtocolOnSearch, ClientSearchResponse>
  ) = GenericOnPollService(messageService, responseStorageService, transformer)

  @Bean
  @Qualifier("InitResults")
  fun initResultReplyService(
    @Autowired messageService: MessageService,
    @Autowired responseStorageService: ResponseStorageService<ProtocolOnInit>,
    @Qualifier("InitTransformer")transformer: GenericOnPollTransformer<ProtocolOnInit, ClientInitResponse>
  ) = GenericOnPollService(messageService, responseStorageService, transformer)
}