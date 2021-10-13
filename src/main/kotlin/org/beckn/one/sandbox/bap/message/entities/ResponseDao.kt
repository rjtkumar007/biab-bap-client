package org.beckn.one.sandbox.bap.message.entities

import org.beckn.protocol.schemas.Default


interface BecknResponseDao {
  val context: ContextDao?
  val error: ErrorDao?
}

data class OnUserDao @Default constructor(
  override val context: ContextDao? = null,
  val user: UserDao,
  override val error: ErrorDao? = null
) : BecknResponseDao


