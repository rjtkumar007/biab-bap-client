package org.beckn.one.sandbox.bap.services

import arrow.core.Either
import org.beckn.one.sandbox.bap.domain.Subscriber
import org.beckn.one.sandbox.bap.errors.registry.RegistryLookupError
import org.beckn.one.sandbox.bap.external.registry.RegistryServiceClient
import org.beckn.one.sandbox.bap.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.external.registry.SubscriberLookupRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class RegistryService(
  @Autowired val registryServiceClient: RegistryServiceClient,
  @Value("\${context.domain}") var domain: String,
  @Value("\${context.city}") var city: String,
  @Value("\${context.country}") var country: String
) {
  fun lookupGateways(): Either<RegistryLookupError, List<SubscriberDto>> {
    val request = SubscriberLookupRequest(
      type = Subscriber.Type.BG,
      domain = domain,
      city = city,
      country = country
    )
    try {
      val httpResponse = registryServiceClient.lookup(request).execute()
      if (httpResponse.code() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
        return Either.Left(RegistryLookupError.RegistryError)
      }
      return Either.Right(httpResponse.body()!!)
    } catch (e: IOException) {
      return Either.Left(RegistryLookupError.RegistryError)
    }
  }
}
