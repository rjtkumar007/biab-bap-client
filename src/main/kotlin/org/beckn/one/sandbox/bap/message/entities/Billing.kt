package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default


data class Billing  @Default constructor(
  val name: String,
  val phone: String,
  val organization: Organization? = null,
  val address: Address? = null,
  val email: String? = null,
  val time: Time? = null,
  val taxNumber: String? = null,
  val createdAt: java.time.OffsetDateTime? = null,
  val updatedAt: java.time.OffsetDateTime? = null
)

data class Organization  @Default constructor(
  val name: String? = null,
  val cred: String? = null
)

