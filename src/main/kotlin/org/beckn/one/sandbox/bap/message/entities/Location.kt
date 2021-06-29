package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class Location @Default constructor(
  val id: String? = null,
  val descriptor: Descriptor? = null,
  val gps: String? = null,
  val address: Address? = null,
  val stationCode: String? = null,
  val city: City? = null,
  val country: Country? = null,
  val circle: Circle? = null,
  val polygon: String? = null,
  val `3dspace`: String? = null
)

data class City @Default constructor(
  val name: String? = null,
  val code: String? = null
)

data class Country @Default constructor(
  val name: String? = null,
  val code: String? = null
)

data class Circle @Default constructor(
  val radius: Scalar? = null
)

data class Scalar @Default constructor(
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

data class ProtocolScalarRange @Default constructor(
  val min: java.math.BigDecimal? = null,
  val max: java.math.BigDecimal? = null
)