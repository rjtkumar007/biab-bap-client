package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default


data class ProtocolBilling @Default constructor(
  val name: String,
  val phone: String,
  val organization: ProtocolOrganization? = null,
  val address: ProtocolAddress? = null,
  val email: String? = null,
  val time: ProtocolTime? = null,
  val taxNumber: String? = null,
  val createdAt: java.time.OffsetDateTime? = null,
  val updatedAt: java.time.OffsetDateTime? = null
)

data class ProtocolOrganization @Default constructor(
  val name: String? = null,
  val cred: String? = null
)

