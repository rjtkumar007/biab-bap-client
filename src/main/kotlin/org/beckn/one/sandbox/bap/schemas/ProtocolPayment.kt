package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

data class ProtocolPayment @Default constructor(
  val uri: java.net.URI? = null,
  val tlMethod: TlMethod? = null,
  val params: Map<String, String>? = null,
  val type: Type? = null,
  val status: Status? = null,
  val time: ProtocolTime? = null
) {

  /**
   *
   * Values: get,post
   */
  enum class TlMethod(val value: String) {
    GET("http/get"),
    POST("http/post");
  }
  /**
   *
   * Values: oNMinusORDER,pREMinusFULFILLMENT,oNMinusFULFILLMENT,pOSTMinusFULFILLMENT
   */
  enum class Type(val value: String) {
    ONORDER("ON-ORDER"),
    PREFULFILLMENT("PRE-FULFILLMENT"),
    ONFULFILLMENT("ON-FULFILLMENT"),
    POSTFULFILLMENT("POST-FULFILLMENT");
  }
  /**
   *
   * Values: pAID,nOTMinusPATD
   */
  enum class Status(val value: String) {
    PAID("PAID"),
    NOTPATD("NOT-PATD");
  }
}