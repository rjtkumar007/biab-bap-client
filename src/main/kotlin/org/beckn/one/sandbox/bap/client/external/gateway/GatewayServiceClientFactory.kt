package org.beckn.one.sandbox.bap.client.external.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Service
class GatewayClientFactory @Autowired constructor(val objectMapper: ObjectMapper) {

  @Cacheable("gatewayClients")
  fun getClient(gatewayUri: String): GatewayClient {
    val retrofit = Retrofit.Builder()
      .baseUrl(gatewayUri)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .build()
    return retrofit.create(GatewayClient::class.java)
  }
}

