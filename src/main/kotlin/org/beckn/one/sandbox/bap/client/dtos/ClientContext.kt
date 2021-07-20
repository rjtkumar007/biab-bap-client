package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.Default

data class ClientContext @Default constructor(
  val transactionId: String,
)
