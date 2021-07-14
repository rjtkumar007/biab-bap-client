package org.beckn.one.sandbox.bap.client.factories

import org.beckn.one.sandbox.bap.schemas.*

class SearchRequestFactory {
  companion object {
    fun create(
      context: ProtocolContext,
      providerId: String?,
      location: String?
    ): ProtocolSearchRequest {
      return ProtocolSearchRequest(
        context = context,
        message = ProtocolSearchRequestMessage(
          intent = ProtocolIntent(
            provider = ProtocolProvider(id = providerId),
            fulfillment = ProtocolFulfillment(
              end = ProtocolFulfillmentEnd(
                location = ProtocolLocation(
                  gps = location
                )
              )
            )
          )
        )
      )
    }
  }
}