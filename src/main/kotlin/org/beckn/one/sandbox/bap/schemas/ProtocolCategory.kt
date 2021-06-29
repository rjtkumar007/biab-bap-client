package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default
import java.time.LocalDateTime

data class ProtocolCategory @Default constructor(
    val id: String,
    val parentCategoryId: String? = null,
    val descriptor: ProtocolDescriptor,
    val time: LocalDateTime? = null,
    val tags: Map<String, String>? = null
)