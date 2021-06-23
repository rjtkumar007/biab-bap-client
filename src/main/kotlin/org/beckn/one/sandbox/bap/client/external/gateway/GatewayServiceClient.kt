package org.beckn.one.sandbox.bap.client.external.gateway

import org.beckn.one.sandbox.bap.schemas.ProtocolResponse
import org.beckn.one.sandbox.bap.schemas.Intent
import org.beckn.one.sandbox.bap.schemas.Request
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GatewayServiceClient {
  @POST("search")
  fun search(@Body request: Request<Intent>): Call<ProtocolResponse>
}