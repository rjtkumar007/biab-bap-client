package org.beckn.one.sandbox.bap.client.daos

import org.beckn.one.sandbox.bap.Default
import org.beckn.one.sandbox.bap.schemas.ProtocolScalarRange

data class ScalarDao @Default constructor(
  val value: java.math.BigDecimal,
  val unit: String,
  val type: Type? = null,
  val estimatedValue: java.math.BigDecimal? = null,
  val computedValue: java.math.BigDecimal? = null,
  val range: ProtocolScalarRange? = null
) {

  enum class Type(val value: String) {
    CONSTANT("CONSTANT"),
    VARIABLE("VARIABLE");
  }
}