package org.beckn.one.sandbox.bap.schemas

import org.litote.kmongo.json

data class Intent(
  val queryString: String?,
  val fulfillment: ProtocolFulfillment?
){
  override fun equals(other: Any?): Boolean {
    if (other != null) {
      return this.json == other.json
    }
    return false
  }
}
