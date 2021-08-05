package org.beckn.one.sandbox.bap.client.external.provider

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.retrofit.RetryCallAdapter
import io.github.resilience4j.retry.Retry
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
  ) {
  private val retry: Retry = RetryFactory.create(
    "BppClient",
    maxAttempts,
    initialIntervalInMillis,
    intervalMultiplier
  )

  @Cacheable("bppClients")
  fun getClient(bppUri: String): BppClient {
    val url = if (bppUri.endsWith("/")) bppUri else "$bppUri/"
    val retrofit = Retrofit.Builder()
      .baseUrl(url)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .addCallAdapterFactory(RetryCallAdapter.of(retry))
      .build()
    return retrofit.create(BppClient::class.java)
  }
}