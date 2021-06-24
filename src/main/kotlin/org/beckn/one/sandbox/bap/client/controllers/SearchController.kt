package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.ClientSearchResponse
import org.beckn.one.sandbox.bap.client.services.SearchService
import org.beckn.one.sandbox.bap.schemas.*
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
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
  @RequestMapping("/v1/search")
  @ResponseBody
  fun searchV1(@RequestParam(required = false) searchString: String?): ResponseEntity<ProtocolResponse> =
    searchService.search(contextFactory.create(), searchString)

  @RequestMapping("/v0/search")
  @ResponseBody
  fun searchV0(@RequestParam(required = false) searchString: String? = ""): ResponseEntity<ProtocolResponse> {
    return ResponseEntity.ok(ProtocolResponse(context = contextFactory.create(), message = ResponseMessage.ack()))
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
            ProtocolItem(
              id = "it-1",
              parentItemId = "pid-1",
              descriptor = ProtocolDescriptor(name = "Very sweet", images = listOf("/imageA", "/imageB")),
              price = ProtocolPrice(
                currency = "Rupee",
                value = "3",
                estimatedValue = "1",
                computedValue = "2",
                listedValue = "3",
                offeredValue = "4",
                minimumValue = "5",
                maximumValue = "6"
              ),
              categoryId = "cat-1",
              tags = mapOf("classifiedAs" to "grocery"),
              matched = true, related = true, recommended = true
            )
          ),
          categories = listOf(
            ProtocolCategory(id = "cat-1", descriptor = ProtocolDescriptor(name = "Grocery items")),
            ProtocolCategory(
              id = "cat-2",
              descriptor = ProtocolDescriptor(name = "Perishable items")
            )
          ),
          offers = listOf(),
        )
      )
    )
  }

  @RequestMapping("/v1/on_search")
  @ResponseBody
  fun onSearchV1(@RequestParam messageId: String): ResponseEntity<ClientSearchResponse> =
    searchService.onSearch(contextFactory.create(messageId = messageId))
}