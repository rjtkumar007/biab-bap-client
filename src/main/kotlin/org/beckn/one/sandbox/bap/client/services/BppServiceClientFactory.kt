package org.beckn.one.sandbox.bap.client.services

import com.fasterxml.jackson.databind.ObjectMapper
import org.beckn.one.sandbox.bap.client.external.provider.BppServiceClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Service
class BppServiceClientFactory @Autowired constructor(val objectMapper: ObjectMapper) {
  fun getClient(bppUri: String): BppServiceClient {
    val retrofit = Retrofit.Builder().baseUrl(bppUri)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper)).build()
    return retrofit.create(BppServiceClient::class.java)
  }
}