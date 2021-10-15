package org.beckn.one.sandbox.bap.client.shared.dtos

import org.beckn.one.sandbox.bap.message.entities.BillingDao
import org.beckn.one.sandbox.bap.message.entities.BillingDetailsDao
import org.beckn.one.sandbox.bap.message.entities.DeliveryAddressDao
import org.beckn.one.sandbox.bap.message.entities.UserDao
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError

data class AccountDetailsResponse(
  override val context: ProtocolContext?,
  val userId: String?,
  override val error: ProtocolError?,
  val userPhone: String? = null,
  val userEmail: String? = null,
  val userName: String? = null,
  val deliveryAddresses : List<DeliveryAddressDao> ? = null,
  val billingInfo : List<BillingDetailsDao>? = null,
  ):ClientResponse


