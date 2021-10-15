package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.one.sandbox.bap.message.entities.BillingDao
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError

data class BillingDetailsResponse(
  override val context: ProtocolContext? ,
  val billing: BillingDao?,
  val userId: String?,
  override val error: ProtocolError?,
  ):ClientResponse


