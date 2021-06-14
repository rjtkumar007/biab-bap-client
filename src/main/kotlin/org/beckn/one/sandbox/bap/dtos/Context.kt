package org.beckn.one.sandbox.bap.dtos

import java.time.Duration

data class Context(
  val domain: String,
  val country: String,
  val city: String,
  val action: Action,
  val core_version: String,
  val bap_id: String,
  val bap_uri: String,
  val bpp_id: String,
  val bpp_uri: String,
  val transaction_id: String,
  val message_id: String,
  val timestamp: String,
  val key: String,
  val ttl: Duration
)