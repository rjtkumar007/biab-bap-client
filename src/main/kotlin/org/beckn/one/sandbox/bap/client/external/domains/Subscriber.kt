package org.beckn.one.sandbox.bap.client.external.domains

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.OffsetDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer
import java.time.LocalDateTime

data class Subscriber constructor(
  val subscriber_id: String,
  val subscriber_url: String,
  val type: Type,
  val domain: String,
  val city: String,
  val country: String,
  val signing_public_key: String,
  val encr_public_key: String,
  @field:JsonSerialize(contentUsing = OffsetDateTimeSerializer::class)
  @field:JsonDeserialize(contentUsing = OffsetDateTimeDeserializer::class)
  val valid_from: LocalDateTime = LocalDateTime.now(),
  @field:JsonSerialize(contentUsing = OffsetDateTimeSerializer::class)
  @field:JsonDeserialize(contentUsing = OffsetDateTimeDeserializer::class)
  val valid_until: LocalDateTime = LocalDateTime.now(),
  val status: Status,
  val created: LocalDateTime = LocalDateTime.now(),
  val updated: LocalDateTime = LocalDateTime.now()
) {
  enum class Type {
    BAP, BPP, BG, LREG, CREG, RREG
  }

  enum class Status {
    INITIATED, UNDER_SUBSCRIPTION, SUBSCRIBED, EXPIRED, UNSUBSCRIBED, INVALID_SSL
  }
}
