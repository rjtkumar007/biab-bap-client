package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

interface ProtocolRequest {
  val context: ProtocolContext
}

data class ProtocolSearchRequest @Default constructor(
  override val context: ProtocolContext,
  val message: ProtocolSearchRequestMessage
) : ProtocolRequest

data class ProtocolSearchRequestMessage @Default constructor(
  val intent: ProtocolIntent
)

data class ProtocolSelectRequest @Default constructor(
  override val context: ProtocolContext,
  val message: ProtocolSelectRequestMessage
): ProtocolRequest

data class ProtocolSelectRequestMessage @Default constructor(
  val selected: ProtocolSelectMessageSelected
)
