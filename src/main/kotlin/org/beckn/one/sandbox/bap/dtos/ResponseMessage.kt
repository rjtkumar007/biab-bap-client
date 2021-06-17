package org.beckn.one.sandbox.bap.dtos

data class ResponseMessage(val ack: Ack) {
  companion object {
    fun ack(): ResponseMessage = ResponseMessage(Ack(ResponseStatus.ACK))
    fun nack(): ResponseMessage = ResponseMessage(Ack(ResponseStatus.NACK))
  }
}