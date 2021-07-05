package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.TimeDao
import org.beckn.one.sandbox.bap.message.entities.TimeRangeDao
import org.beckn.one.sandbox.bap.schemas.ProtocolTime
import org.beckn.one.sandbox.bap.schemas.ProtocolTimeRange
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

object ProtocolTimeFactory {
  fun fixedTimestamp(label: String) = ProtocolTime(
    label = label,
    timestamp = OffsetDateTime.now(fixedClock),
  )

  fun fixedDuration(label: String) = ProtocolTime(
    label = label,
    duration = "5",
  )

  fun fixedDays(label: String) = ProtocolTime(
    label = label,
    days = "3",
  )

  fun fixedRange(label: String) = ProtocolTime(
    label = label,
    range = ProtocolTimeRange(
      start = OffsetDateTime.now(fixedClock),
      end = OffsetDateTime.now(fixedClock).plusDays(2)
    )
  )

  fun timeAsEntity(protocol: ProtocolTime?) = protocol?.let {
    TimeDao(
      label = protocol.label,
      timestamp = protocol.timestamp,
      duration = protocol.duration,
      days = protocol.days,
      range = protocol.range?.let {
        TimeRangeDao(
          start = it.start,
          end = it.end
        )
      }
    )
  }

}

val fixedClock: Clock = Clock.fixed(
  Instant.parse("2018-11-30T18:35:24.00Z"),
  ZoneId.of("Asia/Calcutta")
)