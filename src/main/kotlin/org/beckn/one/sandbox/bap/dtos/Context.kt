package org.beckn.one.sandbox.bap.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

data class Context(
  val domain: String,
  val country: String,
  val city: String,
  val action: Action,
  val core_version: String,
  val bap_id: String? = null,
  val bap_uri: String? = null,
  val bpp_id: String? = null,
  val bpp_uri: String? = null,
  val transaction_id: String = "d4d65ff8-0d60-49bf-9288-a07261bb3f29",
  val message_id: String = "d883b720-2100-4a4e-a779-8685cd501e94",
  @JsonIgnore
  val clock: Clock = Clock.systemUTC(),
  val timestamp: LocalDateTime = LocalDateTime.now(clock),
  val key: String? = null,
  val ttl: Duration? = null,
)