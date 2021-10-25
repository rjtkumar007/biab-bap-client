package org.beckn.one.sandbox.bap.client.external.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.retrofit.CircuitBreakerCallAdapter
import io.github.resilience4j.retrofit.RetryCallAdapter
import io.github.resilience4j.retry.Retry
import okhttp3.OkHttpClient
import org.beckn.one.sandbox.bap.client.shared.Util
import org.beckn.one.sandbox.bap.client.shared.security.SignRequestInterceptor
import org.beckn.one.sandbox.bap.factories.CircuitBreakerFactory
import org.beckn.one.sandbox.bap.factories.RetryFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit


@Service
class GatewayClientFactory @Autowired constructor(
  val objectMapper: ObjectMapper,
  @Value("\${gateway_service.retry.max_attempts}") val maxAttempts: Int,
  @Value("\${gateway_service.retry.initial_interval_in_millis}") val initialIntervalInMillis: Long,
  @Value("\${gateway_service.retry.interval_multiplier}") val intervalMultiplier: Double,
  @Value("\${beckn.security.enabled}") val enableSecurity: Boolean,
  @Value("\${gateway_service.timeouts.connection_in_seconds}") private val connectionTimeoutInSeconds: Long,
  @Value("\${gateway_service.timeouts.read_in_seconds}") private val readTimeoutInSeconds: Long,
  @Value("\${gateway_service.timeouts.write_in_seconds}") private val writeTimeoutInSeconds: Long,
  private val interceptor: SignRequestInterceptor
) {
  @Cacheable("gatewayClients")
  fun getClient(gatewayUri: String): GatewayClient {
    val url : String = Util.getBaseUri(gatewayUri)
    val retrofit = Retrofit.Builder()
      .baseUrl(url)
      .client(buildHttpClient())
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .addCallAdapterFactory(RetryCallAdapter.of(getRetryConfig(gatewayUri)))
      .addCallAdapterFactory(CircuitBreakerCallAdapter.of(CircuitBreakerFactory.create(gatewayUri)))
      .build()
    return retrofit.create(GatewayClient::class.java)
  }

  private fun buildHttpClient(): OkHttpClient {
    val httpClientBuilder = OkHttpClient.Builder()
      .connectTimeout(connectionTimeoutInSeconds, TimeUnit.SECONDS)
      .readTimeout(readTimeoutInSeconds, TimeUnit.SECONDS)
      .writeTimeout(writeTimeoutInSeconds, TimeUnit.SECONDS)
    if (enableSecurity) {
      httpClientBuilder.addInterceptor(interceptor)
    }
    return httpClientBuilder.build()
  }

  private fun getRetryConfig(gatewayUri: String): Retry {
    return RetryFactory.create(
      gatewayUri,
      maxAttempts,
      initialIntervalInMillis,
      intervalMultiplier
    )
  }
}

