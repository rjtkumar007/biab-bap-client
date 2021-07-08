package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.ContextDao
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import java.time.Instant
import java.util.*

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
    timestamp =  Date.from(Instant.now(fixedClock))
  )

  fun fixedAsEntity(context: ProtocolContext) = ContextDao(
    domain = context.domain,
    country = context.country,
    action = ContextDao.Action.values().first { it.value == context.action?.value },
    city = context.city,
    coreVersion = context.coreVersion,
    bapId = context.bapId,
    bapUri = context.bapUri,
    transactionId = context.transactionId,
    messageId = context.messageId,
    timestamp = Date.from(Instant.now(fixedClock))
  )

}