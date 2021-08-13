package org.beckn.one.sandbox.bap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Profile

@EnableCaching
@SpringBootApplication
class SandboxBapApplication

fun main(args: Array<String>) {
  runApplication<SandboxBapApplication>(*args)
}
