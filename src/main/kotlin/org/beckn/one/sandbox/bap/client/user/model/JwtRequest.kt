package org.beckn.one.sandbox.bap.client.user.model

import java.io.Serializable

class JwtRequest(val username: String?,val password: String?) : Serializable {

    companion object {
        private const val serialVersionUID = 5926468583005150707L
    }
}