package org.beckn.one.sandbox.bap.auth

import org.springframework.security.core.userdetails.UserDetailsService
import kotlin.Throws
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Service
import java.util.ArrayList

@Service
class JwtUserDetailsService : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
//        return if ("beckn-bap-client" == username) {
//            User(
//                "javainuse", "$2a$10\$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
//                ArrayList()
//            )
//        } else {
//            throw UsernameNotFoundException("User not found with username: $username")
//        }
      return User(
        "javainuse", "$2a$10\$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
        ArrayList()
      )
    }
}