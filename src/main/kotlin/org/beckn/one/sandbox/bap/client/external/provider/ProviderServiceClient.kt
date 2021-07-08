package org.beckn.one.sandbox.bap.client.external.provider

import org.beckn.one.sandbox.bap.schemas.ProtocolAckResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolSelectRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ProviderServiceClient {
  @POST("select")
  fun select(@Body request: ProtocolSelectRequest): Call<ProtocolAckResponse>
}
