package org.beckn.one.sandbox.bap.message.entities

import org.beckn.protocol.schemas.Default
import org.bson.codecs.pojo.annotations.BsonId

data class UserDao @Default constructor(
  val userId: String? = null,
  val phoneNumber: String? = null,
  val email: String? = null,
  val name: String? = null
)
