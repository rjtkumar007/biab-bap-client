package org.beckn.one.sandbox.bap.client.external.gateway

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.core.IntervalFunction
import io.github.resilience4j.retrofit.RetryCallAdapter
import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig.custom
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.server.WebServerException
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeoutException


@Service
class GatewayClientFactory @Autowired constructor(
  val objectMapper: ObjectMapper,
  @Value("\${gateway_service.retry.max_attempts}") val maxAttempts: Int,
  @Value("\${gateway_service.retry.initial_interval_in_millis}") val initialIntervalInMillis: Long,
  @Value("\${gateway_service.retry.interval_multiplier}") val intervalMultiplier: Double,
) {
  private val retry: Retry = Retry.of("GatewayClient", custom<Response<String>>()
    .maxAttempts(maxAttempts)
    .intervalFunction(
      IntervalFunction
        .ofExponentialBackoff(initialIntervalInMillis, intervalMultiplier)
    )
    .retryOnResult { response -> response.code() == 500 }
    .retryExceptions(IOException::class.java, TimeoutException::class.java, WebServerException::class.java)
    .failAfterMaxAttempts(true)
    .build())

  @Cacheable("gatewayClients")
  fun getClient(gatewayUri: String): GatewayClient {
    val retrofit = Retrofit.Builder()
      .baseUrl(gatewayUri)
      .addConverterFactory(JacksonConverterFactory.create(objectMapper))
      .addCallAdapterFactory(RetryCallAdapter.of(retry))
      .build()
    return retrofit.create(GatewayClient::class.java)
  }
}

