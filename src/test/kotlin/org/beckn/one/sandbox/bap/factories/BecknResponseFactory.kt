package org.beckn.one.sandbox.bap.factories

import org.beckn.one.sandbox.bap.dtos.Ack
import org.beckn.one.sandbox.bap.dtos.BecknResponse
import org.beckn.one.sandbox.bap.dtos.ResponseMessage
import org.beckn.one.sandbox.bap.dtos.ResponseStatus

class BecknResponseFactory {
  companion object {
    fun getDefault() = BecknResponse(
      context = ContextFactory.getDefaultContext(), message = ResponseMessage(
        Ack(ResponseStatus.ACK)
      )
    )
  }
}