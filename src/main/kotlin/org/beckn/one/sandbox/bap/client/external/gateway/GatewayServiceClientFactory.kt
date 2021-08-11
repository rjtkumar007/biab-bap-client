package org.beckn.one.sandbox.bap.client.external.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.retrofit.CircuitBreakerCallAdapter
import io.github.resilience4j.retrofit.RetryCallAdapter
import io.github.resilience4j.retry.Retry
import okhttp3.OkHttpClient
import org.beckn.one.sandbox.bap.client.shared.security.SignRequestInterceptor
import org.beckn.one.sandbox.bap.factories.CircuitBreakerFactory
import org.beckn.one.sandbox.bap.factories.RetryFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory


@Service
class GatewayClientFactory @Autowired constructor(
  val objectMapper: ObjectMapper,
  @Value("\${gateway_service.retry.max_attempts}") val maxAttempts: Int,
  @Value("\${gateway_service.retry.initial_interval_in_millis}") val initialIntervalInMillis: Long,
  @Value("\${gateway_service.retry.interval_multiplier}") val intervalMultiplier: Double,
  @Value("\${beckn.security.enabled}") val enableSecurity: Boolean,
  private val interceptor: SignRequestInterceptor
) {
  @Cacheable("gatewayClients")
  fun getClient(gatewayUri: String): GatewayClient {
    val retry: Retry = RetryFactory.create(
      gatewayUri,
      maxAttempts,
      initialIntervalInMillis,
      intervalMultiplier
    )
    val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
    val circuitBreaker = CircuitBreakerFactory.create(gatewayUri)
    val retrofitBuilder = Retrofit.Builder()
      .baseUrl(gatewayUri)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .addCallAdapterFactory(RetryCallAdapter.of(retry))
      .addCallAdapterFactory(CircuitBreakerCallAdapter.of(circuitBreaker))
    val retrofit = if(enableSecurity) retrofitBuilder.client(okHttpClient).build() else retrofitBuilder.build()
    return retrofit.create(GatewayClient::class.java)
  }
}

