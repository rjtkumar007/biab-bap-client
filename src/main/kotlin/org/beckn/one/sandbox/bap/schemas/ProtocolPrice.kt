package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default


data class ProtocolPrice @Default constructor(
  val currency: String,
  val value: String,
  val estimatedValue: String? = null,
  val computedValue: String? = null,
  val listedValue: String? = null,
  val offeredValue: String? = null,
  val minimumValue: String? = null,
  val maximumValue: String? = null,
)

