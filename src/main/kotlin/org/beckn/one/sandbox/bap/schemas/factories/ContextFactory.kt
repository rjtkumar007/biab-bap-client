package org.beckn.one.sandbox.bap.schemas.factories

import org.beckn.one.sandbox.bap.schemas.Action
import org.beckn.one.sandbox.bap.schemas.Context
import org.beckn.one.sandbox.bap.protocol.ProtocolVersion
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Clock

@Component
class ContextFactory @Autowired constructor(
  @Value("\${context.domain}") private val domain: String,
  @Value("\${context.city}") private val city: String,
  @Value("\${context.country}") private val country: String,
  @Value("\${context.bap_id}") private val bapId: String,
  @Value("\${context.bap_url}") private val bapUrl: String,
  private val uuidFactory: UuidFactory,
  private val clock: Clock = Clock.systemUTC()
) {
  fun create() = Context(
    domain = domain,
    country = country,
    city = city,
    action = Action.search,
    coreVersion = ProtocolVersion.V0_9_1.value,
    bapId = bapId,
    bapUri = bapUrl,
    transactionId = uuidFactory.create(),
    messageId = uuidFactory.create(),
    clock = clock,
  )
}