package org.beckn.one.sandbox.bap.schemas

data class ProtocolFulfillment (
  val id: String? = null,
  val type: String? = null,
  val state: ProtocolState? = null,
  val tracking: Boolean? = null,
  val agent: ProtocolPerson? = null,
  val vehicle: ProtocolVehicle? = null,
  val start: ProtocolFulfillmentStart? = null,
  val end: ProtocolFulfillmentEnd? = null,
  val purpose: String? = null,
  val tags: Map<String, String>? = null
)


data class ProtocolState (
  val descriptor: ProtocolDescriptor? = null,
  val updatedAt: java.time.OffsetDateTime? = null,
  val updatedBy: String? = null
)

data class ProtocolPerson (
  val name: ProtocolName? = null,
  val image: String? = null,
  val dob: java.time.LocalDate? = null,
  val gender: String? = null,
  val cred: String? = null,
  val tags: Map<String, String>? = null
)

data class ProtocolName (
  val full: String? = null,
  val additionalName: String? = null,
  val familyName: String? = null,
  val givenName: String? = null,
  val callSign: String? = null,
  val honorificPrefix: String? = null,
  val honorificSuffix: String? = null
)

data class ProtocolVehicle (
  val category: String? = null,
  val capacity: Int? = null,
  val make: String? = null,
  val model: String? = null,
  val size: String? = null,
  val variant: String? = null,
  val color: String? = null,
  val energyType: String? = null,
  val registration: String? = null
)
// TODO Similar classes
data class ProtocolFulfillmentStart (
  val location: ProtocolLocation? = null,
  val time: ProtocolTime? = null,
  val instructions: ProtocolDescriptor? = null,
  val contact: ProtocolContact? = null
)

// TODO Similar classes
data class ProtocolFulfillmentEnd (
  val location: ProtocolLocation? = null,
  val time: ProtocolTime? = null,
  val instructions: ProtocolDescriptor? = null,
  val contact: ProtocolContact? = null
)


data class ProtocolContact (
  val phone: String? = null,
  val email: String? = null,
  val tags: Map<String, String>? = null
)