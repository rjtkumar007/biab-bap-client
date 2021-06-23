package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.schemas.ProtocolCatalog
import org.beckn.one.sandbox.bap.schemas.Context
import org.beckn.one.sandbox.bap.schemas.Error

interface ClientResponse {
  val context: Context
  val error: Error?
}

data class ClientSearchResponse(
  override val context: Context,
  val message: List<ProtocolCatalog>? = null,
  override val error: Error? = null,
) : ClientResponse