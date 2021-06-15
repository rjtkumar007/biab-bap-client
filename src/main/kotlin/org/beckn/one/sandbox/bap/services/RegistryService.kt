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

@Service
class RegistryService {
  @Autowired
  lateinit var registryServiceClient: RegistryServiceClient

  @Value("\${context.domain}")
  lateinit var domain: String

  @Value("\${context.city}")
  lateinit var city: String

  @Value("\${context.country}")
  lateinit var country: String

  fun lookupGateways(): Either<RegistryLookupError, List<SubscriberDto>> {
    val httpResponse = registryServiceClient.lookup(
      SubscriberLookupRequest(
        type = Subscriber.Type.BG,
        domain = domain,
        city = city,
        country = country
      )
    ).execute()
    if (httpResponse.code() == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
      return Either.Left(RegistryLookupError.InternalServerError)
    }
    return Either.Right(httpResponse.body()!!)
  }
}
