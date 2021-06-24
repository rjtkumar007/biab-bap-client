package org.beckn.one.sandbox.bap.message.entities

import org.beckn.one.sandbox.bap.Default

data class Error @Default constructor(

  val type: Type,
  val code: String,
  val path: String? = null,
  val message: String? = null
) {
  enum class Type(val value: kotlin.String) {
    CONTEXTERROR("CONTEXT-ERROR"),
    COREERROR("CORE-ERROR"),
    DOMAINERROR("DOMAIN-ERROR"),
    POLICYERROR("POLICY-ERROR"),
    JSONSCHEMAERROR("JSON-SCHEMA-ERROR");
  }
}