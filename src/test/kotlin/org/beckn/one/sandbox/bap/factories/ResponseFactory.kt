package org.beckn.one.sandbox.bap.factories

import org.beckn.one.sandbox.bap.dtos.Ack
import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.dtos.ResponseMessage
import org.beckn.one.sandbox.bap.dtos.ResponseStatus

class ResponseFactory {
  companion object {
    fun getDefault(contextFactory: ContextFactory) = Response(
      context = contextFactory.create(), message = ResponseMessage(Ack(ResponseStatus.ACK))
    )
  }
}