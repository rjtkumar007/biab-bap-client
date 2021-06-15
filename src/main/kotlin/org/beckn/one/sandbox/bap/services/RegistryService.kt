package org.beckn.one.sandbox.bap.services

import org.beckn.one.sandbox.bap.domain.Subscriber
import org.beckn.one.sandbox.bap.external.registry.RegistryServiceClient
import org.beckn.one.sandbox.bap.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.external.registry.SubscriberLookupRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import retrofit2.Call

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

  fun lookupGateways(): retrofit2.Response<List<SubscriberDto>> {
    val lookupResponse: Call<List<SubscriberDto>> = registryServiceClient.lookup(
      SubscriberLookupRequest(
        type = Subscriber.Type.BG,
        domain = domain,
        city = city,
        country = country
      )
    )
    return lookupResponse.execute()
  }
}
