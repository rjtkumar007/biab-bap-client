package org.beckn.one.sandbox.bap.common.factories

import org.beckn.one.sandbox.bap.schemas.Ack
import org.beckn.one.sandbox.bap.schemas.Response
import org.beckn.one.sandbox.bap.schemas.ResponseMessage
import org.beckn.one.sandbox.bap.schemas.ResponseStatus
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory

class ResponseFactory {
  companion object {
    fun getDefault(contextFactory: ContextFactory) = Response(
      context = contextFactory.create(), message = ResponseMessage(Ack(ResponseStatus.ACK))
    )
  }
}