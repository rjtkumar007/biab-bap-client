package org.beckn.one.sandbox.bap.client.shared.dtos

import com.fasterxml.jackson.annotation.JsonIgnore
import org.beckn.one.sandbox.bap.message.entities.AddDeliveryAddressDao
import org.beckn.one.sandbox.bap.message.entities.BillingDetailsDao
import org.beckn.one.sandbox.bap.message.entities.DeliveryAddressDao
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError

data class AccountDetailsResponse(
  override val context: ProtocolContext?,
  @JsonIgnore val userId: String?,
  override val error: ProtocolError?,
  val userPhone: String? = null,
  val userEmail: String? = null,
  val userName: String? = null,
  var deliveryAddresses : List<DeliveryAddressResponse> ? = null,
  var billingInfo : List<BillingDetailsResponse>? =  null,
  ):ClientResponse


