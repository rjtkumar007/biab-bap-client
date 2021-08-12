package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.protocol.schemas.Default
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ProtocolOption
import org.beckn.protocol.schemas.ProtocolRatingCategory

data class ClientOrderPolicyResponse @Default constructor(
  override val context: ProtocolContext,
  val message: ClientOrderPolicyResponseMessage? = null,
  override val error: ProtocolError? = null
) : ClientResponse

data class ClientOrderPolicyResponseMessage @Default constructor(
  val cancellationReasons: List<ProtocolOption>? = null,
  val ratingCategories: List<ProtocolRatingCategory>? = null
)

data class ClientOrderPolicyMultipleResponse @Default constructor(
  val context: ProtocolContext,
  var message: ClientOrderPolicyResponseMessage? = null,
  var error: MutableList<ProtocolError>? = mutableListOf()
)
