package org.beckn.one.sandbox.bap.client.factories

import org.beckn.protocol.schemas.*

class SearchRequestFactory {
  companion object {

    fun create(
      context: ProtocolContext,
      searchString: String? = null,
      providerId: String? = null,
      location: String? = null,
      categoryId: String? = null,
      providerName: String? = null,
      categoryName: String? = null
    ): ProtocolSearchRequest {
      return ProtocolSearchRequest(
        context = context,
        message = ProtocolSearchRequestMessage(
          intent = ProtocolIntent(
            provider = ProtocolProvider(
              id = providerId,
              category_id = categoryId,
              descriptor = ProtocolDescriptor(name = providerName)
            ),
            category = ProtocolCategory(id = categoryId, descriptor = ProtocolDescriptor(name = categoryName)),
            fulfillment = ProtocolFulfillment(
              end = ProtocolFulfillmentEnd(
                location = ProtocolLocation(
                  gps = location
                )
              ),
              start = ProtocolFulfillmentStart(
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