package org.beckn.one.sandbox.bap.client.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.beckn.one.sandbox.bap.client.external.provider.ProviderServiceClient
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Service
class ProviderServiceClientFactory @Autowired constructor(val objectMapper: ObjectMapper) {
  fun getClient(provider: SubscriberDto): ProviderServiceClient { //todo: should this be anything other than subscriberDto?
    val retrofit = Retrofit.Builder().baseUrl(provider.subscriber_url)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper)).build()
    return retrofit.create(ProviderServiceClient::class.java)
  }
}