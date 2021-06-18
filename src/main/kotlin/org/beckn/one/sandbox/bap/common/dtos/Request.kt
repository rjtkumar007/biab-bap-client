package org.beckn.one.sandbox.bap.common.dtos

data class Request<M>(
  val context: Context,
  val message: M
)