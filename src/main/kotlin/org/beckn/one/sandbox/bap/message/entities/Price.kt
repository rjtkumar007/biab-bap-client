package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class Price @Default constructor(
  val currency: String? = null,
  val value: String? = null,
  val listedValue: String? = null,
  val minimumValue: String? = null,
  val maximumValue: String? = null
)
