package org.beckn.one.sandbox.bap.client.external.gateway

import org.beckn.one.sandbox.bap.common.dtos.Response
import org.beckn.one.sandbox.bap.common.dtos.Intent
import org.beckn.one.sandbox.bap.common.dtos.Request
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GatewayServiceClient {
  @POST("search")
  fun search(@Body request: Request<Intent>): Call<Response>
}