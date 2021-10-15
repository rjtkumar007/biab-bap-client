package org.beckn.one.sandbox.bap.message.entities

import org.beckn.protocol.schemas.Default
import org.bson.codecs.pojo.annotations.BsonId

data class UserDao @Default constructor(
  val userId: String,
  val userPhone: String? = null,
  val userEmail: String? = null,
  val userName: String? = null
)
