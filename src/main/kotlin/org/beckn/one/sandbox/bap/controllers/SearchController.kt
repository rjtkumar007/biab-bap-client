package org.beckn.one.sandbox.bap.controllers

import org.beckn.one.sandbox.bap.dtos.Response
import org.beckn.one.sandbox.bap.services.SearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(@Autowired val searchService: SearchService) {

  @RequestMapping("/search")
  @ResponseBody
  fun search(@RequestParam searchString: String): ResponseEntity<Response> {
    return searchService.search()
  }
}