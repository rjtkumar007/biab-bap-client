package org.beckn.one.sandbox.bap.auth.utils

import org.beckn.one.sandbox.bap.auth.model.User
import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtil {

  fun getSecuredUserDetail(): User? {
    val authentication = SecurityContextHolder.getContext().authentication
    return if (authentication.isAuthenticated) {
      return try {
        authentication.principal as User
      } catch (exception: Exception) {
        null
      }
    } else {
      null
    }
  }


}