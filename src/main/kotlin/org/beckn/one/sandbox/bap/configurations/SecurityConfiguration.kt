package org.beckn.one.sandbox.bap.configurations

import okhttp3.OkHttpClient
import org.beckn.one.sandbox.bap.client.shared.security.SignRequestInterceptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecurityConfiguration {

  @Bean
  fun okHttpClient(@Autowired interceptor: SignRequestInterceptor): OkHttpClient =
    OkHttpClient.Builder().addInterceptor(interceptor).build()


}