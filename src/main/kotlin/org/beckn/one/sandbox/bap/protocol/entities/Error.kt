package org.beckn.one.sandbox.bap.protocol.entities

data class Error (

  val type: Type,
  val code: String,
  val path: String? = null,
  val message: String? = null
) {
  enum class Type(val value: kotlin.String){
    CONTEXTERROR("CONTEXT-ERROR"),
    COREERROR("CORE-ERROR"),
    DOMAINERROR("DOMAIN-ERROR"),
    POLICYERROR("POLICY-ERROR"),
    JSONSCHEMAERROR("JSON-SCHEMA-ERROR");
  }
}