package org.beckn.one.sandbox.bap.protocol.schemas

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime

data class Context(
  val domain: String,
  val country: String,
  val city: String,
  val action: Action,
  val coreVersion: String,
  val bapId: String? = null,
  val bapUri: String? = null,
  val bppId: String? = null,
  val bppUri: String? = null,
  val transactionId: String = "d4d65ff8-0d60-49bf-9288-a07261bb3f29",
  val messageId: String = "d883b720-2100-4a4e-a779-8685cd501e94",
  @JsonIgnore val clock: Clock = Clock.systemUTC(),
  val timestamp: LocalDateTime = LocalDateTime.now(clock),
  val key: String? = null,
  val ttl: Duration? = null,
)