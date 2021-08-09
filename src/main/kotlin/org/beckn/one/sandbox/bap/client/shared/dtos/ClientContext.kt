package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.protocol.schemas.Default

data class ClientContext @Default constructor(
  val transactionId: String = UuidFactory().create(),
  val bppId: String? = null,
)