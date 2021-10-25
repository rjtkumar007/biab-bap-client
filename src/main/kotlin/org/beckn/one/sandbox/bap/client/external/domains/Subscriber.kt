package org.beckn.one.sandbox.bap.client.external.domains

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.time.LocalDateTime
import java.time.OffsetDateTime

data class Subscriber constructor(
  val subscriber_id: String,
  val subscriber_url: String,
  val type: Type,
  val domain: String,
  val city: String,
  val country: String,
  val signing_public_key: String,
  val encr_public_key: String,
  val valid_from: OffsetDateTime = OffsetDateTime.now(),
  val valid_until: OffsetDateTime = OffsetDateTime.now(),
  val status: Status,
  val created: OffsetDateTime = OffsetDateTime.now(),
  val updated: OffsetDateTime = OffsetDateTime.now()
) {
  enum class Type {
    BAP, BPP, BG, LREG, CREG, RREG
  }

  enum class Status {
    INITIATED, UNDER_SUBSCRIPTION, SUBSCRIBED, EXPIRED, UNSUBSCRIBED, INVALID_SSL
  }
}
