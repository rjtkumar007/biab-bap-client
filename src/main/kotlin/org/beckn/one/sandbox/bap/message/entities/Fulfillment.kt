package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class Fulfillment  @Default constructor(
  val id: String? = null,
  val type: String? = null,
  val state: State? = null,
  val tracking: Boolean? = null,
  val agent: Person? = null,
  val vehicle: Vehicle? = null,
  val start: FulfillmentStart? = null,
  val end: FulfillmentEnd? = null,
  val purpose: String? = null,
  val tags: Map<String, String>? = null
)


data class State  @Default constructor(
  val descriptor: Descriptor? = null,
  val updatedAt: java.time.OffsetDateTime? = null,
  val updatedBy: String? = null
)

data class Person  @Default constructor(
  val name: Name? = null,
  val image: String? = null,
  val dob: java.time.LocalDate? = null,
  val gender: String? = null,
  val cred: String? = null,
  val tags: Map<String, String>? = null
)

data class Name  @Default constructor(
  val full: String? = null,
  val additionalName: String? = null,
  val familyName: String? = null,
  val givenName: String? = null,
  val callSign: String? = null,
  val honorificPrefix: String? = null,
  val honorificSuffix: String? = null
)

data class Vehicle  @Default constructor(
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
data class FulfillmentStart  @Default constructor(
  val location: Location? = null,
  val time: Time? = null,
  val instructions: Descriptor? = null,
  val contact: Contact? = null
)

// TODO Similar classes
data class FulfillmentEnd  @Default constructor(
  val location: Location? = null,
  val time: Time? = null,
  val instructions: Descriptor? = null,
  val contact: Contact? = null
)


data class Contact  @Default constructor(
  val phone: String? = null,
  val email: String? = null,
  val tags: Map<String, String>? = null
)