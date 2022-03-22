package org.beckn.one.sandbox.bap.client.external.bap

import org.beckn.protocol.schemas.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProtocolClient {
  @GET("protocol/response/v1/on_search")
  fun getSearchResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnSearch>>

  @GET("protocol/response/v1/on_select")
  fun getSelectResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnSelect>>

  @GET("protocol/response/v1/on_init")
  fun getInitResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnInit>>

  @GET("protocol/response/v1/on_confirm")
  fun getConfirmResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnConfirm>>

  @GET("protocol/response/v1/on_track")
  fun getTrackResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnTrack>>

  @GET("protocol/response/v1/on_support")
  fun getSupportResponseCall(@Query("messageId") messageId: String): Call<List<ProtocolOnSupport>>

  @GET("protocol/response/v1/on_rating")
  fun getRatingResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnRating>>

  @GET("protocol/response/v1/on_status")
  fun getOrderStatusResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnOrderStatus>>

  @GET("protocol/response/v1/on_cancel")
  fun getCancelResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnCancel>>

  @GET("protocol/response/v1/on_order_status")
  fun getOrderByIdStatusResponsesCall(@Query("orderId") orderId: String): Call<List<ProtocolOnOrderStatus>>

  @GET("protocol/response/v1/on_cancellation_reasons")
  fun getOnCancellationReasonsResponsesCall(@Query("messageId") messageId: String): Call<List<ProtocolOnCancellationReasons>>
}