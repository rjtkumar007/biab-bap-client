package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.one.sandbox.bap.message.entities.DeliveryAddressDao
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError

data class DeliveryAddressResponse(
  override val context: ProtocolContext?,
  val userId: String?,
  val address: DeliveryAddressDao?,
  override val error: ProtocolError?,
  ):ClientResponse


