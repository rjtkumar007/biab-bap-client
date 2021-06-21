package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.services.SearchService
import org.beckn.one.sandbox.bap.common.dtos.*
import org.beckn.one.sandbox.bap.common.factories.ContextFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController @Autowired constructor(
  val searchService: SearchService,
  val contextFactory: ContextFactory
) {
  val log: Logger = LoggerFactory.getLogger(SearchService::class.java)

  @RequestMapping("/v1/search")
  @ResponseBody
  fun searchV1(@RequestParam searchString: String): ResponseEntity<Response> {
    log.info("Got search request: {}", searchString)
    return searchService.search(contextFactory.create(), searchString)
  }

  @RequestMapping("/v0/search")
  @ResponseBody
  fun searchV0(@RequestParam(required = false) searchString: String? = ""): ResponseEntity<Response> {
    return ResponseEntity.ok(Response(contextFactory.create(), ResponseMessage.ack()))
  }

  @RequestMapping("/v0/on_search")
  @ResponseBody
  fun onSearchV0(@RequestParam(required = false) messageId: String? = ""): ResponseEntity<List<ProviderWithItems>> {
    return ResponseEntity.ok(
      listOf(
        ProviderWithItems(
          provider = Provider(
            id = "p1", name = "LocalBaniya", locations = listOf("Camp", "Shastri Nagar"),
            descriptor = "Your local grocery", images = listOf("/localbaniya")
          ),
          items = listOf(
            Item(
              id = "it-1", name = "Sugar", parentItemId = "pid-1", descriptor = "Very sweet",
              price = Price(
                estimated = "1", computed = "2", listed = "3", offered = "4", minimum = "5", maximum = "6"
              ),
              categoryId = "cat-1", images = listOf("/imageA", "/imageB"),
              tags = mapOf("classifiedAs" to "grocery"),
              attributes = mapOf("colour" to "white")
            )
          ),
          categories = listOf(
            Category(id = "cat-1", name = "Groceries", descriptor = "Grocery items"),
            Category(id = "cat-2", name = "Perishable Items", descriptor = "Perishable items")
          ),
          offers = listOf(),
        )
      )
    )
  }
}