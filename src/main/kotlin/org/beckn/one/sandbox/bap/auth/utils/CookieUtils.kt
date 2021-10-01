package org.beckn.one.sandbox.bap.auth.utils

import org.beckn.one.sandbox.bap.auth.model.SecurityProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.util.WebUtils
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class CookieUtils {
    @Autowired
    var httpServletRequest: HttpServletRequest? = null

    @Autowired
    var httpServletResponse: HttpServletResponse? = null

    @Autowired
    var restSecProps: SecurityProperties? = null
    fun getCookie(name: String?): Cookie? {
        return WebUtils.getCookie(httpServletRequest!!, name!!)
    }

    fun setCookie(name: String?, value: String?, expiryInMinutes: Int) {
        val expiresInSeconds = expiryInMinutes * 60 * 60
        val cookie = Cookie(name, value)
        cookie.secure = restSecProps!!.cookieProps!!.secure
        cookie.path = restSecProps!!.cookieProps!!.path
        cookie.domain = restSecProps!!.cookieProps!!.domain
        cookie.maxAge = expiresInSeconds
        httpServletResponse!!.addCookie(cookie)
    }

    fun setSecureCookie(name: String?, value: String?, expiryInMinutes: Int) {
        val expiresInSeconds = expiryInMinutes * 60 * 60
        val cookie = Cookie(name, value)
        cookie.isHttpOnly = restSecProps!!.cookieProps!!.httpOnly
        cookie.secure = restSecProps!!.cookieProps!!.secure
        cookie.path = restSecProps!!.cookieProps!!.path
        cookie.domain = restSecProps!!.cookieProps!!.domain
        cookie.maxAge = expiresInSeconds
        httpServletResponse!!.addCookie(cookie)
    }

    fun setSecureCookie(name: String?, value: String?) {
        val expiresInMinutes = restSecProps!!.cookieProps!!.maxAgeInMinutes
        setSecureCookie(name, value, expiresInMinutes)
    }

    fun deleteSecureCookie(name: String?) {
        val expiresInSeconds = 0
        val cookie = Cookie(name, null)
        cookie.isHttpOnly = restSecProps!!.cookieProps!!.httpOnly
        cookie.secure = restSecProps!!.cookieProps!!.secure
        cookie.path = restSecProps!!.cookieProps!!.path
        cookie.domain = restSecProps!!.cookieProps!!.domain
        cookie.maxAge = expiresInSeconds
        httpServletResponse!!.addCookie(cookie)
    }

    fun deleteCookie(name: String?) {
        val expiresInSeconds = 0
        val cookie = Cookie(name, null)
        cookie.path = restSecProps!!.cookieProps!!.path
        cookie.domain = restSecProps!!.cookieProps!!.domain
        cookie.maxAge = expiresInSeconds
        httpServletResponse!!.addCookie(cookie)
    }
}