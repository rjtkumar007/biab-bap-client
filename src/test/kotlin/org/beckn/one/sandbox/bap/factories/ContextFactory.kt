package org.beckn.one.sandbox.bap.factories

import org.beckn.one.sandbox.bap.constants.City
import org.beckn.one.sandbox.bap.constants.Country
import org.beckn.one.sandbox.bap.constants.Domain
import org.beckn.one.sandbox.bap.constants.ProtocolVersion
import org.beckn.one.sandbox.bap.dtos.Action
import org.beckn.one.sandbox.bap.dtos.Context
import java.time.Duration

class ContextFactory {
  companion object {
    fun getDefaultContext() = Context(
      domain = Domain.LocalRetail.value,
      country = Country.India.value,
      city = City.Bengaluru.value,
      action = Action.search,
      core_version = ProtocolVersion.V0_9_1.value,
      bap_id = "",
      bap_uri = "",
      bpp_id = "",
      bpp_uri = "",
      transaction_id = "",
      message_id = "",
      timestamp = "",
      key = "",
      ttl = Duration.ofMinutes(1)
    )
  }
}