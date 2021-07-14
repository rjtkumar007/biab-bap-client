package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.Default
import org.beckn.one.sandbox.bap.schemas.ProtocolLocation

data class DeliveryDto @Default constructor(
  val phone: String,
  val email: String,
  val location: ProtocolLocation
)