package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

data class ProtocolError @Default constructor(val code: String, val message: String)