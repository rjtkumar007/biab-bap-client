package org.beckn.one.sandbox.bap.user.model

import java.io.Serializable

class JwtRequest : Serializable {
    var username: String? = null
    var password: String? = null


    constructor(username: String?, password: String?) {
        this.username = username
        this.password = password
    }

    companion object {
        private const val serialVersionUID = 5926468583005150707L
    }
}