package org.beckn.one.sandbox.bap.controller
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder;

@Controller
class SearchController
{
  @PostMapping("/on_search")
  fun postOnSearch(@RequestBody request: Any, uri:UriComponentsBuilder): ResponseEntity<Any> {
    println(request)
    val headers = HttpHeaders()
    return ResponseEntity(headers, HttpStatus.OK)
  }
}
