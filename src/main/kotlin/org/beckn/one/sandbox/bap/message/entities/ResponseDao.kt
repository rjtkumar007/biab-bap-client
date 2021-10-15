package org.beckn.one.sandbox.bap.message.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.beckn.protocol.schemas.Default


interface BecknResponseDao {
  val context: ContextDao?
  val error: ErrorDao?
  val userId: String?
}

data class OnUserDao @Default constructor(
  override val context: ContextDao? = null,
  val user: UserDao,
  override val error: ErrorDao? = null,
  override val userId: String?
) : BecknResponseDao


data class AddDeliveryAddressDao @Default constructor(
    @field:JsonIgnore
  override val context: ContextDao? = null,
    override val userId: String?,
    val address: DeliveryAddressDao,
    @field:JsonIgnore
  override val error: ErrorDao? = null
) : BecknResponseDao

data class BillingDetailsDao @Default constructor(
  @field:JsonIgnore
  override val context: ContextDao? = null,
  val billing: BillingDao,
  @field:JsonIgnore
  override val error: ErrorDao? = null,
  override val userId: String?
  ) : BecknResponseDao
