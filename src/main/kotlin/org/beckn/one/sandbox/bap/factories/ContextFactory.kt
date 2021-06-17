package org.beckn.one.sandbox.bap.factories

import org.beckn.one.sandbox.bap.ProtocolVersion
import org.beckn.one.sandbox.bap.dtos.Action
import org.beckn.one.sandbox.bap.dtos.Context
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
) {
  fun create(clock: Clock = Clock.systemUTC()) = Context(
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