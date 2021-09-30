package org.beckn.one.sandbox.bap.user.controller


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.beckn.one.sandbox.bap.auth.utils.JwtTokenUtil
import org.beckn.one.sandbox.bap.auth.JwtUserDetailsService
import kotlin.Throws
import org.beckn.one.sandbox.bap.user.model.JwtRequest
import org.springframework.http.ResponseEntity
import org.beckn.one.sandbox.bap.user.model.JwtResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.*
import java.lang.Exception

@RestController
@CrossOrigin
class JwtAuthenticationController {
    @Autowired
    private val authenticationManager: AuthenticationManager? = null

    @Autowired
    private val jwtTokenUtil: JwtTokenUtil? = null

    @Autowired
    private val userDetailsService: JwtUserDetailsService? = null

    @RequestMapping(value = ["/client/v1/authenticate"], method = [RequestMethod.POST])
    fun createAuthenticationToken(@RequestBody authenticationRequest: JwtRequest): ResponseEntity<*> {
//        authenticate(authenticationRequest.username, authenticationRequest.password)
      return if(authenticationRequest!=null && !authenticationRequest.username.isNullOrEmpty()){
          val userDetails = userDetailsService?.loadUserByUsername(authenticationRequest.username!!)
          val token = jwtTokenUtil?.generateToken(userDetails!!)
        ResponseEntity.ok(JwtResponse(token!!))
      }else{
        ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .body(HttpStatus.BAD_REQUEST)
      }
    }

    @Throws(Exception::class)
    private fun authenticate(username: String?, password: String?) {
        try {
            authenticationManager!!.authenticate(UsernamePasswordAuthenticationToken(username, password))
        } catch (e: DisabledException) {
            throw Exception("USER_DISABLED", e)
        } catch (e: BadCredentialsException) {
            throw Exception("INVALID_CREDENTIALS", e)
        }
    }
}