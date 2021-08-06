package org.beckn.one.sandbox.bap.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.retrofit.RetryCallAdapter
import io.github.resilience4j.retry.Retry
import org.beckn.one.sandbox.bap.client.external.bap.ProtocolClient
import org.beckn.one.sandbox.bap.factories.RetryFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

@Configuration
class BapClientConfiguration(
  @Autowired @Value("\${protocol_service.url}")
  private val bapServiceUrl: String,
  @Value("\${protocol_service.retry.max_attempts}")
  private val maxAttempts: Int,
  @Value("\${protocol_service.retry.initial_interval_in_millis}")
  private val initialIntervalInMillis: Long,
  @Value("\${protocol_service.retry.interval_multiplier}")
  private val intervalMultiplier: Double,
  @Autowired
  private val objectMapper: ObjectMapper
) {
  @Bean
  fun bapClient(): ProtocolClient {
    val retry: Retry = RetryFactory.create(
      "BapClient",
      maxAttempts,
      initialIntervalInMillis,
      intervalMultiplier
    )
    val retrofit = Retrofit.Builder()
      .baseUrl(bapServiceUrl)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .addCallAdapterFactory(RetryCallAdapter.of(retry))
      .build()

    return retrofit.create(ProtocolClient::class.java)
  }

}