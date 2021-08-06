package org.beckn.one.sandbox.bap.client.external

import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ResponseStatus
import org.springframework.http.HttpStatus
import retrofit2.Response

fun <T> Response<T>.isInternalServerError() = this.code() == HttpStatus.INTERNAL_SERVER_ERROR.value()

fun <T> Response<T>.hasBody() = this.body() != null

fun Response<ProtocolAckResponse>.isAckNegative() = this.body()!!.message.ack.status == ResponseStatus.NACK