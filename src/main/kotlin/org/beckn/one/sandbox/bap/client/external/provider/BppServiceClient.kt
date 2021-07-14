package org.beckn.one.sandbox.bap.client.external.provider

import org.beckn.one.sandbox.bap.schemas.ProtocolAckResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolInitRequest
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchRequest
import org.beckn.one.sandbox.bap.schemas.ProtocolSelectRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface BppServiceClient {
  @POST("search")
  fun search(@Body request: ProtocolSearchRequest): Call<ProtocolAckResponse>

  @POST("select")
  fun select(@Body request: ProtocolSelectRequest): Call<ProtocolAckResponse>


  @POST("init")
  fun init(@Body request: ProtocolInitRequest): Call<ProtocolAckResponse>
}
