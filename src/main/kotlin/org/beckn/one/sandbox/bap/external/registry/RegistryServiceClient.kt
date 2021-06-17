package org.beckn.one.sandbox.bap.external.registry

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistryServiceClient {
  @POST("lookup")
  fun lookup(@Body request: SubscriberLookupRequest): Call<List<SubscriberDto>>
}