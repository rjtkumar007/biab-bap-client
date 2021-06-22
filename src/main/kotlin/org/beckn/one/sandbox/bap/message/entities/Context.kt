package org.beckn.one.sandbox.bap.message.entities

data class Context (

  val domain: Domain,
  val country: String,
  val city: String,
  val action: Action,
  val coreVersion: String,
  val bapId: String,
  val bapUri: String,
  val bppId: String? = null,
  val bppUri: String? = null,
  val transactionId: String,
  val messageId: String,
  val timestamp: java.time.LocalDateTime,
  val key: String? = null,
  val ttl: Duration? = null
) {



  enum class Action(val value: String){
    SEARCH("search"),
    SELECT("select"),
    INIT("init"),
    CONFIRM("confirm"),
    UPDATE("update"),
    STATUS("status"),
    TRACK("track"),
    CANCEL("cancel"),
    FEEDBACK("feedback"),
    SUPPORT("support"),
    ONSEARCH("on_search"),
    ONSELECT("on_select"),
    ONINIT("on_init"),
    ONCONFIRM("on_confirm"),
    ONUPDATE("on_update"),
    ONSTATUS("on_status"),
    ONTRACK("on_track"),
    ONCANCEL("on_cancel"),
    ONFEEDBACK("on_feedback"),
    ONSUPPORT("on_support"),
    ACK("ack");
  }
}

typealias Domain = String
typealias Duration = String