package org.beckn.one.sandbox.bap.client.external.registry

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Clock
import java.time.LocalDateTime

data class SubscriberDto(
  val subscriber_id: String,
  val subscriber_url: String,
  val type: Type,
  val domain: String,
  val city: String,
  val country: String,
  val signing_public_key: String,
  val encr_public_key: String,
  val status: Status,

  @JsonIgnore
  val clock: Clock = Clock.systemUTC(),
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") val valid_from: LocalDateTime = LocalDateTime.now(clock),
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") val valid_until: LocalDateTime = LocalDateTime.now(clock),
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") val created: LocalDateTime = LocalDateTime.now(clock),
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS") val updated: LocalDateTime = LocalDateTime.now(clock)
) {

  enum class Type {
    BAP, BPP, BG, LREG, CREG, RREG
  }

  enum class Status {
    INITIATED, UNDER_SUBSCRIPTION, SUBSCRIBED, EXPIRED, UNSUBSCRIBED, INVALID_SSL
  }
}

