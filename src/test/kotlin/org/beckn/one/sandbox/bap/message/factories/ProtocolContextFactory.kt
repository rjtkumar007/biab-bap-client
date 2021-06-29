package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Context
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import java.time.LocalDateTime

object ProtocolContextFactory {

  val fixed = ProtocolContext(
    domain = "LocalRetail",
    country = "IN",
    action = ProtocolContext.Action.SEARCH,
    city = "Pune",
    coreVersion = "0.9.1-draft03",
    bapId = "http://host.bap.com",
    bapUri = "http://host.bap.com",
    transactionId = "222",
    messageId = "222",
    timestamp = LocalDateTime.now(fixedClock)
  )

  fun fixedAsEntity(context: ProtocolContext) = Context(
    domain = context.domain,
    country = context.country,
    action = Context.Action.values().first { it.value == context.action.value },
    city = context.city,
    coreVersion = context.coreVersion,
    bapId = context.bapId,
    bapUri = context.bapUri,
    transactionId = context.transactionId,
    messageId = context.messageId,
    timestamp = LocalDateTime.now(fixedClock)
  )

}