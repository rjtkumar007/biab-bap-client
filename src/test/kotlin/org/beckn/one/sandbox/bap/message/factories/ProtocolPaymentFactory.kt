package org.beckn.one.sandbox.bap.message.factories

import org.beckn.one.sandbox.bap.message.entities.Payment
import org.beckn.one.sandbox.bap.schemas.ProtocolPayment
import java.net.URI

object ProtocolPaymentFactory {

  fun create() = ProtocolPayment(
    uri = URI("http://host.pay.co.in"),
    tlMethod = ProtocolPayment.TlMethod.POST,
    params = mapOf("nonce" to "JXNASN"),
    type = ProtocolPayment.Type.ONORDER,
    status = ProtocolPayment.Status.PAID,
    time = ProtocolTimeFactory.fixedTimestamp("Paid on")
  )

  fun createAsEntity(protocol: ProtocolPayment?) = protocol?.let {
    Payment(
      uri = protocol.uri,
      tlMethod = Payment.TlMethod.values().first { it.value == protocol.tlMethod?.value },
      params = protocol.params,
      type = Payment.Type.values().first { it.value == protocol.type?.value },
      status = Payment.Status.values().first { it.value == protocol.status?.value },
      time = ProtocolTimeFactory.timeAsEntity(protocol.time)
    )
  }
}