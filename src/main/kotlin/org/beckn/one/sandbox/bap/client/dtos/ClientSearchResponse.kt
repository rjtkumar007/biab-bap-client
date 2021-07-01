package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.schemas.ProtocolError
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.Default
import org.beckn.one.sandbox.bap.schemas.*
import java.time.LocalDateTime

interface ClientResponse {
  val context: ProtocolContext
  val error: ProtocolError?
}

data class ClientErrorResponse(
  override val context: ProtocolContext,
  override val error: ProtocolError? = null
): ClientResponse

data class ClientSearchResponse(
  override val context: ProtocolContext,
  val message: ClientSearchResponseMessage? = null,
  override val error: ProtocolError? = null,
) : ClientResponse

data class ClientSearchResponseMessage(
  val catalogs: List<ClientCatalog>? = null,
)

data class ClientCatalog @Default constructor(
  val bppDescriptor: ProtocolDescriptor? = null,
  val bppProviders: List<ProtocolProviderCatalog>? = null,
  val bppCategories: List<ProtocolCategory>? = null,
  val exp: LocalDateTime? = null
)
