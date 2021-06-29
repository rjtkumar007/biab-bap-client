package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Provider
import org.beckn.one.sandbox.bap.schemas.ProtocolProvider

object ProtocolProviderFactory {

  fun create(id: Int) = ProtocolProvider(
    id = "Provider-$id",
    descriptor = ProtocolDescriptorFactory.create("Retail-provider", id),
    time = ProtocolTimeFactory.fixedTimestamp("fixed-time"),
    locations = listOf(
      ProtocolLocationFactory.cityLocation(1),
      ProtocolLocationFactory.cityLocation(2).copy(city = ProtocolCityFactory.pune)
    ),
    tags = mapOf("key 1" to "value 1")
  )

  fun createAsEntity(protocol: ProtocolProvider?) = protocol?.let {
    Provider(
      id = protocol.id,
      descriptor = ProtocolDescriptorFactory.createAsEntity(protocol.descriptor),
      time = ProtocolTimeFactory.timeAsEntity(protocol.time),
      locations = protocol.locations?.map { ProtocolLocationFactory.locationEntity(it) },
      tags = protocol.tags
    )
  }
}