package org.beckn.one.sandbox.bap.auth.service

import org.beckn.one.sandbox.bap.auth.model.Credentials
import org.beckn.one.sandbox.bap.auth.model.SecurityProperties
import org.beckn.one.sandbox.bap.auth.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.beckn.one.sandbox.bap.auth.utils.CookieUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import javax.servlet.http.HttpServletRequest

@Service
class SecurityService {
    @Autowired
    var httpServletRequest: HttpServletRequest? = null

    @Autowired
    var cookieUtils: CookieUtils? = null

    @Autowired
    var securityProps: SecurityProperties? = null
    val user: User?
        get() {
            var userPrincipal: User? = null
            val securityContext = SecurityContextHolder.getContext()
            val principal = securityContext.authentication.principal
            if (principal is User) {
                userPrincipal = principal
            }
            return userPrincipal
        }
    val credentials: Credentials
        get() {
            val securityContext = SecurityContextHolder.getContext()
            return securityContext.authentication.credentials as Credentials
        }
    val isPublic: Boolean
        get() = securityProps!!.allowedPublicApis!!.contains(httpServletRequest!!.requestURI)

    fun getBearerToken(request: HttpServletRequest): String? {
        var bearerToken: String? = null
        val authorization = request.getHeader("Authorization")
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            bearerToken = authorization.substring(7)
        }
        return bearerToken
    }
}