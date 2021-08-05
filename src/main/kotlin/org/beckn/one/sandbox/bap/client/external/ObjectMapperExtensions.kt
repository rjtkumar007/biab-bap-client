package org.beckn.one.sandbox.bap.client.external

import com.fasterxml.jackson.databind.ObjectMapper


fun <T> ObjectMapper.toJson(instance: T): String = this.writeValueAsString(instance)
