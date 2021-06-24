package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.Error
import org.beckn.one.sandbox.bap.schemas.ProtocolCatalog

interface ClientResponse {
  val context: ProtocolContext
  val error: Error?
}

data class ClientSearchResponse(
  override val context: ProtocolContext,
  val message: ClientSearchResponseMessage? = null,
  override val error: Error? = null,
) : ClientResponse

data class ClientSearchResponseMessage(
  val catalogs: List<ProtocolCatalog>? = null,
)