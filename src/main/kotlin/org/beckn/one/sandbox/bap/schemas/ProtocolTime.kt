package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

data class ProtocolTime @Default constructor(
  val label: String? = null,
  val timestamp: java.time.OffsetDateTime? = null,
  val duration: String? = null,
  val range: ProtocolTimeRange? = null,
  val days: String? = null
)

data class ProtocolTimeRange @Default constructor(
  val start: java.time.OffsetDateTime? = null,
  val end: java.time.OffsetDateTime? = null
)


