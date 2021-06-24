package org.beckn.one.sandbox.bap.schemas

import com.fasterxml.jackson.annotation.JsonIgnore
import org.beckn.one.sandbox.bap.Default
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime

data class ProtocolContext @Default constructor(
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
) {
  enum class Action(val value: String) {
    SEARCH("search"),
    SELECT("select"),
    INIT("`init`"),
    CONFIRM("confirm"),
    UPDATE("update"),
    STATUS("status"),
    TRACK("track"),
    CANCEL("cancel"),
    FEEDBACK("feedback"),
    SUPPORT("support"),
    ON_SEARCH("on_search"),
    ON_SELECT("on_select"),
    ON_INIT("on_init"),
    ON_CONFIRM("on_confirm"),
    ON_UPDATE("on_update"),
    ON_STATUS("on_status"),
    ON_TRACK("on_track"),
    ON_CANCEL("on_cancel"),
    ON_FEEDBACK("on_feedback"),
    ON_SUPPORT("on_support"),
    ACK("ack"),
  }
}