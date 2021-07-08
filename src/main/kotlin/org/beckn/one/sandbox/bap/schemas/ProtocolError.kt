package org.beckn.one.sandbox.bap.schemas

import com.fasterxml.jackson.annotation.JsonProperty
import org.beckn.one.sandbox.bap.Default

data class ProtocolError @Default constructor(
  val code: String,
  val message: String,
  val type: Type? = null,
  val path: String? = null,
){

  enum class Type(val value: String) {
    @JsonProperty("CONTEXT-ERROR") CONTEXTERROR("CONTEXT-ERROR"),
    @JsonProperty("CORE-ERROR") COREERROR("CORE-ERROR"),
    @JsonProperty("DOMAIN-ERROR") DOMAINERROR("DOMAIN-ERROR"),
    @JsonProperty("POLICY-ERROR") POLICYERROR("POLICY-ERROR"),
    @JsonProperty("JSON-SCHEMA-ERROR") JSONSCHEMAERROR("JSON-SCHEMA-ERROR");
  }
}