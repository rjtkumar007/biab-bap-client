package org.beckn.one.sandbox.bap.common.factories

import org.beckn.one.sandbox.bap.common.dtos.Ack
import org.beckn.one.sandbox.bap.common.dtos.Response
import org.beckn.one.sandbox.bap.common.dtos.ResponseMessage
import org.beckn.one.sandbox.bap.common.dtos.ResponseStatus

class ResponseFactory {
  companion object {
    fun getDefault(contextFactory: ContextFactory) = Response(
      context = contextFactory.create(), message = ResponseMessage(Ack(ResponseStatus.ACK))
    )
  }
}