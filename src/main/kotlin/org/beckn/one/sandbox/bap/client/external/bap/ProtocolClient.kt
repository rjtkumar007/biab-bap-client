package org.beckn.one.sandbox.bap.client.external.bap

import org.beckn.protocol.schemas.ProtocolOnConfirm
import org.beckn.protocol.schemas.ProtocolOnInit
import org.beckn.protocol.schemas.ProtocolOnSearch
import org.beckn.protocol.schemas.ProtocolOnSelect
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ProtocolClient {
  @GET("/v1/on_search")
  fun getSearchResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnSearch>>

  @GET("/v1/on_select")
  fun getSelectResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnSelect>>

  @GET("/v1/on_init")
  fun getInitResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnInit>>

  @GET("/v1/on_confirm")
  fun getConfirmResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnConfirm>>
}