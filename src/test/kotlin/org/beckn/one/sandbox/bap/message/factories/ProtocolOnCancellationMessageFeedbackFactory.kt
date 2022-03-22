package org.beckn.one.sandbox.bap.message.factories

import org.beckn.protocol.schemas.ProtocolDescriptor
import org.beckn.protocol.schemas.ProtocolOnCancellationReasonMessage
import org.beckn.protocol.schemas.ProtocolOption

object ProtocolOnCancellationMessageFeedbackFactory {
  fun create(index: Int = 1): ProtocolOnCancellationReasonMessage {
    return ProtocolOnCancellationReasonMessage(
      listOf(ProtocolOption(
      id = "item id $index",
      descriptor = ProtocolDescriptor(name = "item descriptor")
      )
      )
    )
  }
}