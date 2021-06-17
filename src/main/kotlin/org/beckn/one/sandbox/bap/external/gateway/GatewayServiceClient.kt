package org.beckn.one.sandbox.bap.external.gateway

import org.beckn.one.sandbox.bap.dtos.BecknResponse
import org.beckn.one.sandbox.bap.dtos.Intent
import org.beckn.one.sandbox.bap.dtos.Request
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GatewayServiceClient {
  @POST("search")
  fun search(@Body request: Request<Intent>): Call<BecknResponse>
}