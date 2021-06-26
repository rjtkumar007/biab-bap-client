package org.beckn.one.sandbox.bap.configurations

import org.beckn.one.sandbox.bap.client.dtos.ClientSearchResponse
import org.beckn.one.sandbox.bap.client.services.GenericOnPollService
import org.beckn.one.sandbox.bap.client.services.GenericOnPollTransformer
import org.beckn.one.sandbox.bap.message.services.MessageService
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClientServicesConfiguration {

  @Bean
  fun forSearchResults(): GenericOnPollTransformer<ProtocolSearchResponse, ClientSearchResponse> =
    GenericOnPollTransformer.forSearchResults

  @Bean
  fun searchResultReplyService(
    @Autowired messageService: MessageService,
    @Autowired responseStorageService: ResponseStorageService<ProtocolSearchResponse>,
    @Autowired transformer: GenericOnPollTransformer<ProtocolSearchResponse, ClientSearchResponse>
  ) = GenericOnPollService(messageService, responseStorageService, transformer)
}