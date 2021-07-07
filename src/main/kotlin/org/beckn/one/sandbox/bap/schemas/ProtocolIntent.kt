package org.beckn.one.sandbox.bap.schemas

import org.litote.kmongo.json

data class ProtocolIntent(
  val queryString: String?,
  val fulfillment: ProtocolFulfillment?,
  val item: ProtocolIntentItem? = null,
){
  override fun equals(other: Any?): Boolean {
    if (other != null) {
      return this.json == other.json
    }
    return false
  }
}

data class ProtocolIntentItem (
  val id: String? = null,
  val descriptor: ProtocolIntentItemDescriptor? = null
)

data class ProtocolIntentItemDescriptor (
  val name: String? = null,
  val tags: Map<String, String>? = null
)