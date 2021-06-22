package org.beckn.one.sandbox.bap.message.entities

data class Price(
  val currency: String? = null,
  val value: String? = null,
  val listedValue: String? = null,
  val minimumValue: String? = null,
  val maximumValue: String? = null
)
