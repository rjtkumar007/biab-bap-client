package org.beckn.one.sandbox.bap.message.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import org.beckn.protocol.schemas.Default
import java.time.Clock
import java.time.OffsetDateTime


interface BecknResponseDao {
  val context: ContextDao?
  val error: ErrorDao?
  val userId: String?
}

data class OnConfirmDao @Default constructor(
  @field:JsonIgnore override val context: ContextDao,
  val transactionId: String? = null,
  val messageId: String? = null,
  val message: OnConfirmMessageDao? = null,
  @field:JsonIgnore override val error: ErrorDao? = null,
  override val userId: String?
) : BecknResponseDao

data class OnConfirmMessageDao @Default constructor(
  val order: OrderDao? = null
)

data class AddDeliveryAddressDao @Default constructor(
  @field:JsonIgnore
  override val context: ContextDao? = null,
  override val userId: String?,
  @field:JsonIgnore
  override val error: ErrorDao? = null,
  val id: String,
  val descriptor: DescriptorDao? = null,
  val gps: String? = null,
  val default: Boolean? = true,
  val address: AddressDao? = null
) : BecknResponseDao

data class BillingDetailsDao @Default constructor(
  @field:JsonIgnore
  override val context: ContextDao? = null,
  val id: String?,
  val name: String,
  val phone: String,
  val organization: OrganizationDao? = null,
  val address: AddressDao? = null,
  val email: String? = null,
  val time: TimeDao? = null,
  val taxNumber: String? = null,
  val createdAt: java.time.OffsetDateTime? = null,
  val updatedAt: java.time.OffsetDateTime? = null,
  val locationId: String? = null,
  @field:JsonIgnore
  override val error: ErrorDao? = null,
  override val userId: String?
) : BecknResponseDao

data class AccountDetailsDao @Default constructor(
  @field:JsonIgnore
  override val context: ContextDao? = null,
  val userPhone: String? = null,
  val userEmail: String? = null,
  val userName: String? = null,
  val deliveryAddresses: List<DeliveryAddressDao>? = null,
  val billingInfo: List<BillingDetailsDao>? = null,
  @field:JsonIgnore
  override val error: ErrorDao? = null,
  override var userId: String?,
  @JsonIgnore val clock: Clock = Clock.systemUTC(),
  val timestamp: OffsetDateTime = OffsetDateTime.now(clock)
) : BecknResponseDao

