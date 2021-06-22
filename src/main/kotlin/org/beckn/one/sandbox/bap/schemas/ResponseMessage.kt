package org.beckn.one.sandbox.bap.schemas

data class ResponseMessage(val ack: Ack) {
  companion object {
    fun ack(): ResponseMessage = ResponseMessage(Ack(ResponseStatus.ACK))
    fun nack(): ResponseMessage = ResponseMessage(Ack(ResponseStatus.NACK))
  }
}