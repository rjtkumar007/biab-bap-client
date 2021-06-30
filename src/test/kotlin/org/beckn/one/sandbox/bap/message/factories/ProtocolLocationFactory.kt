package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Address
import org.beckn.one.sandbox.bap.message.entities.City
import org.beckn.one.sandbox.bap.message.entities.Country
import org.beckn.one.sandbox.bap.message.entities.Location
import org.beckn.one.sandbox.bap.schemas.ProtocolAddress
import org.beckn.one.sandbox.bap.schemas.ProtocolCity
import org.beckn.one.sandbox.bap.schemas.ProtocolCountry
import org.beckn.one.sandbox.bap.schemas.ProtocolLocation

object ProtocolLocationFactory {

  fun cityLocation(id: Int) = ProtocolLocation(
    id = IdFactory.forLocation(id),
    descriptor = ProtocolDescriptorFactory.create("location", IdFactory.forLocation(id)),
    city = ProtocolCityFactory.bangalore,
    country = ProtocolCountryFactory.india
  )

  fun addressLocation(id: Int) = ProtocolLocation(
    id = IdFactory.forLocation(id),
    descriptor = ProtocolDescriptorFactory.create("location", IdFactory.forLocation(id)),
    address = ProtocolAddress(
      door = "A-11",
      building = "Vedanta",
      street = "High Street",
      areaCode = "435667"
    )
  )

  fun locationEntity(protocol: ProtocolLocation?) = protocol?.let {
    Location(
      id = protocol.id,
      descriptor = ProtocolDescriptorFactory.createAsEntity(protocol.descriptor),
      city = ProtocolCityFactory.cityAsEntity(protocol.city),
      country = ProtocolCountryFactory.countryAsEntity(protocol.country),
      address = protocol.address?.let { a ->
        Address(
          door = a.door,
          building = a.building,
          street = a.street,
          areaCode = a.areaCode,
          name = a.name,
          locality = a.locality,
          ward = a.ward,
          city = a.city,
          state = a.state,
          country = a.country
        )
      }
    )
  }
}

object ProtocolCityFactory {

  val pune = ProtocolCity(
    name = "Pune",
    code = "PUN"
  )


  val bangalore = ProtocolCity(
    name = "Bangalore",
    code = "BLR"
  )

  fun cityAsEntity(protocol: ProtocolCity?) = protocol?.let {
    City(
      name = protocol.name,
      code = protocol.code
    )
  }

}

object ProtocolCountryFactory {
  val india = ProtocolCountry(
    name = "INDIA",
    code = "IN"
  )

  fun countryAsEntity(protocol: ProtocolCountry?) = protocol?.let {
    Country(
      name = protocol.name,
      code = protocol.code
    )
  }
}