package org.beckn.one.sandbox.bap.client.dtos

import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ProtocolQuotation

data class ClientQuoteResponse(
    override val context: ProtocolContext,
    val message: ClientQuoteResponseMessage?,
    override val error: ProtocolError? = null,
) : ClientResponse

data class ClientQuoteResponseMessage(
    val quote: ProtocolQuotation,
)
