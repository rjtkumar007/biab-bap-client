package org.beckn.one.sandbox.bap.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.beckn.one.sandbox.bap.external.gateway.GatewayServiceClient
import org.beckn.one.sandbox.bap.external.registry.SubscriberDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Service
class GatewayServiceClientFactory(@Autowired val objectMapper: ObjectMapper) {
  fun getClient(gateway: SubscriberDto): GatewayServiceClient {
    val retrofit = Retrofit.Builder()
      .baseUrl(gateway.subscriber_url)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .build()
    return retrofit.create(GatewayServiceClient::class.java)
  }
}

