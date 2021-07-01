package org.beckn.one.sandbox.bap.common.factories

import org.beckn.one.sandbox.bap.schemas.ProtocolAck
import org.beckn.one.sandbox.bap.schemas.ProtocolAckResponse
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.beckn.one.sandbox.bap.schemas.ResponseStatus
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory

class ResponseFactory {
  companion object {
    fun getDefault(contextFactory: ContextFactory) = ProtocolAckResponse(
      context = contextFactory.create(), message = ResponseMessage(ProtocolAck(ResponseStatus.ACK))
    )
  }
}