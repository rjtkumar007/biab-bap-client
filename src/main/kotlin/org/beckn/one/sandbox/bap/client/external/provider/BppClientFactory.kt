package org.beckn.one.sandbox.bap.client.external.provider

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Service
class BppClientFactory @Autowired constructor(val objectMapper: ObjectMapper) {
  @Cacheable("bppClients")
  fun getClient(bppUri: String): BppClient {
    val url = if (bppUri.endsWith("/")) bppUri else "$bppUri/"
    val retrofit = Retrofit.Builder().baseUrl(url)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper)).build()
    return retrofit.create(BppClient::class.java)
  }
}