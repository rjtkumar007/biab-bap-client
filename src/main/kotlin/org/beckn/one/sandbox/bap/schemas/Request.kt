package org.beckn.one.sandbox.bap.schemas

data class Request<M>(
  val context: Context,
  val message: M
)