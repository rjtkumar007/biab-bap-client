package org.beckn.one.sandbox.bap.common.factories

import org.beckn.one.sandbox.bap.schemas.*
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory

class ResponseFactory {
  companion object {
    fun getDefault(context: ProtocolContext) = ProtocolAckResponse(
      context = context, message = ResponseMessage(ProtocolAck(ResponseStatus.ACK))
    )
  }
}