package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default


data class ProtocolPrice @Default constructor(
  val currency: String,
  val value: String,
  val estimatedValue: String?,
  val computedValue: String?,
  val listedValue: String?,
  val offeredValue: String?,
  val minimumValue: String?,
  val maximumValue: String?
)

