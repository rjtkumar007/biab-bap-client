package org.beckn.one.sandbox.bap.common.factories

import org.beckn.one.sandbox.bap.protocol.schemas.Ack
import org.beckn.one.sandbox.bap.protocol.schemas.Response
import org.beckn.one.sandbox.bap.protocol.schemas.ResponseMessage
import org.beckn.one.sandbox.bap.protocol.schemas.ResponseStatus
import org.beckn.one.sandbox.bap.protocol.schemas.factories.ContextFactory

class ResponseFactory {
  companion object {
    fun getDefault(contextFactory: ContextFactory) = Response(
      context = contextFactory.create(), message = ResponseMessage(Ack(ResponseStatus.ACK))
    )
  }
}