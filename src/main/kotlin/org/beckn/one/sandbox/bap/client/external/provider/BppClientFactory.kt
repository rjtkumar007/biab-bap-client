package org.beckn.one.sandbox.bap.client.external.provider

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
class BppClientFactory @Autowired constructor(
  val objectMapper: ObjectMapper,
  @Value("\${bpp_service.retry.max_attempts}")
  private val maxAttempts: Int,
  @Value("\${bpp_service.retry.initial_interval_in_millis}")
  private val initialIntervalInMillis: Long,
  @Value("\${bpp_service.retry.interval_multiplier}")
  private val intervalMultiplier: Double,
  @Value("\${beckn.security.enabled}") val enableSecurity: Boolean,
  private val interceptor: SignRequestInterceptor
) {
  @Cacheable("bppClients")
  fun getClient(bppUri: String): BppClient {
    val retry: Retry = RetryFactory.create(
      bppUri,
      maxAttempts,
      initialIntervalInMillis,
      intervalMultiplier
    )
    val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
    val circuitBreaker = CircuitBreakerFactory.create(bppUri)
    val url = if (bppUri.endsWith("/")) bppUri else "$bppUri/"
    val retrofitBuilder = Retrofit.Builder()
      .baseUrl(url)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .addCallAdapterFactory(RetryCallAdapter.of(retry))
      .addCallAdapterFactory(CircuitBreakerCallAdapter.of(circuitBreaker))
    val retrofit = if (enableSecurity) retrofitBuilder.client(okHttpClient).build() else retrofitBuilder.build()
    return retrofit.create(BppClient::class.java)
  }
}