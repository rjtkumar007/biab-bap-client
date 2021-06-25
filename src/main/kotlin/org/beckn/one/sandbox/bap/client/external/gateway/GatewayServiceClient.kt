package org.beckn.one.sandbox.bap.client.external.gateway

import org.beckn.one.sandbox.bap.schemas.ProtocolAckResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GatewayServiceClient {
  @POST("search")
  fun search(@Body request: ProtocolSearchRequest): Call<ProtocolAckResponse>
}