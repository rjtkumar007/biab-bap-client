package org.beckn.one.sandbox.bap.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.core.IntervalFunction
import io.github.resilience4j.retrofit.RetryCallAdapter
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.beckn.one.sandbox.bap.client.external.registry.RegistryClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.server.WebServerException
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpStatus
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeoutException


@Configuration
class RegistryClientConfiguration(
  @Autowired @Value("\${registry_service.url}")
  private val registryServiceUrl: String,
  @Value("\${registry_service.retry.max_attempts}")
  private val maxAttempts: Int,
  @Value("\${registry_service.retry.initial_interval_in_millis}")
  private val initialIntervalInMillis: Long,
  @Value("\${registry_service.retry.interval_multiplier}")
  private val intervalMultiplier: Double,
  @Autowired @Value("\${bpp_registry_service.url}")
  private val bppRegistryServiceUrl: String,
  @Autowired
  private val objectMapper: ObjectMapper
) {
  private val retry: Retry = Retry.of("RegistryClient", RetryConfig.custom<Response<String>>()
    .maxAttempts(maxAttempts)
    .intervalFunction(
      IntervalFunction
        .ofExponentialBackoff(initialIntervalInMillis, intervalMultiplier)
    )
    .retryOnResult { response -> response.code() == HttpStatus.INTERNAL_SERVER_ERROR.value() }
    .retryExceptions(IOException::class.java, TimeoutException::class.java, WebServerException::class.java)
    .failAfterMaxAttempts(true)
    .build())

  @Bean
  @Primary
  fun registryServiceClient(): RegistryClient {
    val retrofit = Retrofit.Builder()
      .baseUrl(registryServiceUrl)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .addCallAdapterFactory(RetryCallAdapter.of(retry))
      .build()

    return retrofit.create(RegistryClient::class.java)
  }

  @Bean(BPP_REGISTRY_SERVICE_CLIENT)
  fun bppRegistryServiceClient(): RegistryClient {
    val retrofit = Retrofit.Builder()
      .baseUrl(bppRegistryServiceUrl)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .build()

    return retrofit.create(RegistryClient::class.java)
  }

  companion object {
    const val BPP_REGISTRY_SERVICE_CLIENT = "bppRegistryServiceClient"

  }
}