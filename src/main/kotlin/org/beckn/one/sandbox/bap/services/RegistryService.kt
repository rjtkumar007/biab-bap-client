package org.beckn.one.sandbox.bap.services

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import org.beckn.one.sandbox.bap.domain.Subscriber
import org.beckn.one.sandbox.bap.errors.registry.RegistryLookupError
import org.beckn.one.sandbox.bap.errors.registry.RegistryLookupError.Internal
import org.beckn.one.sandbox.bap.errors.registry.RegistryLookupError.NoGatewayFound
import org.beckn.one.sandbox.bap.external.registry.RegistryServiceClient
import org.beckn.one.sandbox.bap.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.external.registry.SubscriberLookupRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import retrofit2.Response

@Service
class RegistryService(
  @Autowired val registryServiceClient: RegistryServiceClient,
  @Value("\${context.domain}") val domain: String,
  @Value("\${context.city}") val city: String,
  @Value("\${context.country}") val country: String
) {
  val log: Logger = LoggerFactory.getLogger(RegistryService::class.java)

  fun lookupGateways(): Either<RegistryLookupError, List<SubscriberDto>> {
    return try {
      val request = lookupGatewayRequest()
      log.info("Looking up gateways: {}", request)
      val httpResponse = registryServiceClient.lookup(request).execute()
      log.info("Lookup gateway response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      when {
        internalServerError(httpResponse) -> Left(Internal)
        noGatewaysFound(httpResponse) -> Left(NoGatewayFound)
        else -> Right(httpResponse.body()!!)
      }
    } catch (e: Exception) {
      log.error("Error when looking up gateways", e)
      Left(Internal)
    }
  }

  fun lookupGatewayRequest() = SubscriberLookupRequest(
    type = Subscriber.Type.BG,
    domain = domain,
    city = city,
    country = country
  )

  private fun noGatewaysFound(httpResponse: Response<List<SubscriberDto>>) =
    httpResponse.body() == null || httpResponse.body()?.isEmpty() == true

  private fun internalServerError(httpResponse: Response<List<SubscriberDto>>) =
    httpResponse.code() == HttpStatus.INTERNAL_SERVER_ERROR.value()
}
