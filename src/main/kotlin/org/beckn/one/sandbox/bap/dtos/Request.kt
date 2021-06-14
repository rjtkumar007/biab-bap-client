package org.beckn.one.sandbox.bap.dtos

data class Request<M>(
  val context: Context,
  val message: M
)