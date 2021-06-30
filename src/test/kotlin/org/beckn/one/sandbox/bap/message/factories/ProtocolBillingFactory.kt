package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Address
import org.beckn.one.sandbox.bap.message.entities.Billing
import org.beckn.one.sandbox.bap.message.entities.Organization
import org.beckn.one.sandbox.bap.schemas.ProtocolAddress
import org.beckn.one.sandbox.bap.schemas.ProtocolBilling
import org.beckn.one.sandbox.bap.schemas.ProtocolOrganization

object ProtocolBillingFactory {

  fun create() = ProtocolBilling(
    name = "ICIC",
    phone = "9890098900",
    organization = ProtocolOrganization(name = "IC", cred = "ajsgdysdxasg!!"),
    address = ProtocolAddress(
      door = "A-11",
      building = "Vedanta",
      street = "High Street",
      areaCode = "435667"
    ),
    email = "abc@icic",
    time = ProtocolTimeFactory.fixedDays("Working Days")
  )

  fun createAsEntity(protocol: ProtocolBilling?) = protocol?.let {
    Billing(
      name = protocol.name,
      phone = protocol.phone,
      organization = protocol.organization?.let {
        Organization(name = it.name, cred = it.cred)
      },
      address = protocol.address?.let {
        Address(
          door = it.door,
          building = it.building,
          street = it.street,
          areaCode = it.areaCode
        )
      },
      email = protocol.email,
      time = ProtocolTimeFactory.timeAsEntity(protocol.time)
    )
  }
}