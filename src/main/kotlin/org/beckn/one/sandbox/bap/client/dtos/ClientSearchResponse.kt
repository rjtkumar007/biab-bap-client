package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.one.sandbox.bap.schemas.BecknCatalog
import org.beckn.one.sandbox.bap.schemas.Context
import org.beckn.one.sandbox.bap.schemas.Error

interface ClientResponse {
  val context: Context
  val error: Error?
}

data class ClientSearchResponse(
    override val context: Context,
    val message: List<BecknCatalog>? = null,
    override val error: Error? = null,
) : ClientResponse