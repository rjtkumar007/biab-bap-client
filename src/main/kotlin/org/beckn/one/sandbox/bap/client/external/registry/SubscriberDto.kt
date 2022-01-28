package org.beckn.one.sandbox.bap.client.external.registry

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Clock
import java.time.LocalDateTime
import java.time.OffsetDateTime

data class SubscriberDto(
  val subscriber_id: String,
  val subscriber_url: String,
  val type: Type,
  val domain: String?,
  val city: String?,
  val country: String?,
  val signing_public_key: String,
  val encr_public_key: String,
  val status: Status,

  @JsonIgnore
  val clock: Clock = Clock.systemUTC(),
  val valid_from: OffsetDateTime = OffsetDateTime.now(clock),
  val valid_until: OffsetDateTime = OffsetDateTime.now(clock),
  val created: OffsetDateTime = OffsetDateTime.now(clock),
  val updated: OffsetDateTime = OffsetDateTime.now(clock)
) {

  enum class Type {
    BAP, BPP, BG, LREG, CREG, RREG
  }

  enum class Status {
    INITIATED, UNDER_SUBSCRIPTION, SUBSCRIBED, EXPIRED, UNSUBSCRIBED, INVALID_SSL
  }
}

