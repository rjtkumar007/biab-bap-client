package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

interface Response {
  val context: Context
  val error: Error?
}

data class ProtocolResponse(
  override val context: Context,
  val message: ResponseMessage,
  override val error: Error? = null,
) : Response

data class ProtocolSearchResponse @Default constructor(
  override val context: Context,
  val message: ProtocolSearchResponseMessage? = null,
  override val error: Error? = null,
) : Response

data class ProtocolSearchResponseMessage @Default constructor(
  val catalog: ProtocolCatalog? = null
)
