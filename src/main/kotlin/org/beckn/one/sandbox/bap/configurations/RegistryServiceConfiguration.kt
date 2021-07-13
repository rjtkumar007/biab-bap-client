package org.beckn.one.sandbox.bap.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import org.beckn.one.sandbox.bap.client.external.registry.RegistryServiceClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory


@Configuration
class RegistryServiceConfiguration(
  @Autowired @Value("\${registry_service.url}")
  private val registryServiceUrl: String,
  @Autowired @Value("\${bpp_registry_service.url}")
  private val bppRegistryServiceUrl: String,
  @Autowired
  private val objectMapper: ObjectMapper
) {
  @Bean
  fun registryServiceClient(): RegistryServiceClient {
    val retrofit = Retrofit.Builder()
      .baseUrl(registryServiceUrl)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .build()

    return retrofit.create(RegistryServiceClient::class.java)
  }

  @Bean(BPP_REGISTRY_SERVICE_CLIENT)
  fun bppRegistryServiceClient(): RegistryServiceClient {
    val retrofit = Retrofit.Builder()
      .baseUrl(bppRegistryServiceUrl)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .build()

    return retrofit.create(RegistryServiceClient::class.java)
  }

  companion object {
    const val BPP_REGISTRY_SERVICE_CLIENT = "bppRegistryServiceClient"

  }
}