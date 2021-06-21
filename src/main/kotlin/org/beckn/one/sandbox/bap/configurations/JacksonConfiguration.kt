package org.beckn.one.sandbox.bap.configurations

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.text.DateFormat


@Configuration
class JacksonConfiguration {
  @Bean
  fun objectMapper(): ObjectMapper {
    val objectMapper = ObjectMapper()
    objectMapper.findAndRegisterModules()
    objectMapper.dateFormat = DateFormat.getDateTimeInstance()
    objectMapper.propertyNamingStrategy = PropertyNamingStrategies.SnakeCaseStrategy()
    return objectMapper
  }
}