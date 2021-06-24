package org.beckn.one.sandbox.bap.schemas

import org.beckn.one.sandbox.bap.Default

data class Error @Default constructor(val code: String, val message: String)