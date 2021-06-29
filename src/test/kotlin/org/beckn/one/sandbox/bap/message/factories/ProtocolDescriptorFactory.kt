package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Descriptor
import org.beckn.one.sandbox.bap.schemas.ProtocolDescriptor

object ProtocolDescriptorFactory {

  fun create(type: String, index: Int) = ProtocolDescriptor(
    name = "$type-$index name",
    code = "$type-$index code",
    symbol = "$type-$index symbol",
    shortDesc = "A short description about $type-$index",
    longDesc = "A long description about $type-$index",
    images = listOf("uri:https://$type-$index-image-1.com", "uri:https://$type-$index-image-2.com"),
    audio = "$type-$index-image-audio-file-path",
    threeDRender = "$type-$index-3d"
  )

  fun createAsEntity(protocol: ProtocolDescriptor?) = protocol?.let {
    Descriptor(
      name = protocol.name,
      code = protocol.code,
      symbol = protocol.symbol,
      shortDesc = protocol.shortDesc,
      longDesc = protocol.longDesc,
      images = protocol.images,
      audio = protocol.audio,
      threeDRender = protocol.threeDRender
    )
  }


}