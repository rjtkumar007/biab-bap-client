package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

data class ProtocolError @Default constructor(
  val code: String,
  val message: String,
  val type: Type? = null,
  val path: String? = null,
){

  enum class Type(val value: String) {
    CONTEXTERROR("CONTEXT-ERROR"),
    COREERROR("CORE-ERROR"),
    DOMAINERROR("DOMAIN-ERROR"),
    POLICYERROR("POLICY-ERROR"),
    JSONSCHEMAERROR("JSON-SCHEMA-ERROR");
  }
}