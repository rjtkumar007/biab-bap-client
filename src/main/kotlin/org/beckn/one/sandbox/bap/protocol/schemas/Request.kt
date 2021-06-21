package org.beckn.one.sandbox.bap.protocol.schemas

data class Request<M>(
  val context: Context,
  val message: M
)