package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class Time @Default constructor(
  val label: String? = null,
  val timestamp: java.time.OffsetDateTime? = null,
  val duration: String? = null,
  val range: TimeRange? = null,
  val days: String? = null
)

data class TimeRange @Default constructor(
  val start: java.time.OffsetDateTime? = null,
  val end: java.time.OffsetDateTime? = null
)


