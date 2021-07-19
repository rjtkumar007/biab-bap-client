package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.Default
import org.beckn.one.sandbox.bap.schemas.ProtocolContext

data class GetOrderPolicyDto @Default constructor(
  val context: ProtocolContext
)

