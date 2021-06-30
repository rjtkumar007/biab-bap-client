package org.beckn.one.sandbox.bap.schemas


data class ProtocolBilling (
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


