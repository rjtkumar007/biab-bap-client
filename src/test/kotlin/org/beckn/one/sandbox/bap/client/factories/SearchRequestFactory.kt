package org.beckn.one.sandbox.bap.client.factories

import org.beckn.protocol.schemas.*

class SearchRequestFactory {
  companion object {
    fun create(
      context: ProtocolContext,
      searchString: String? = null,
      providerId: String? = null,
      location: String? = null,
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
            ),
            item = ProtocolIntentItem(descriptor = ProtocolIntentItemDescriptor(name = searchString))
          )
        )
      )
    }
  }
}