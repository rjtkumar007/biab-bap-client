package org.beckn.one.sandbox.bap.common.factories

import org.springframework.stereotype.Component
import java.util.*

@Component
class UuidFactory {
  fun create() = UUID.randomUUID().toString()
}