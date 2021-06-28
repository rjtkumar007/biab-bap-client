package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.ClientResponse
import org.beckn.one.sandbox.bap.client.dtos.ClientSearchResponse
import org.beckn.one.sandbox.bap.client.services.GenericOnPollService
import org.beckn.one.sandbox.bap.schemas.*
import org.beckn.one.sandbox.bap.schemas.factories.ContextFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OnSearchController @Autowired constructor(
  private val onPollService: GenericOnPollService<ProtocolSearchResponse, ClientSearchResponse>,
  private val contextFactory: ContextFactory
): BaseOnPollController<ProtocolSearchResponse, ClientSearchResponse>(onPollService, contextFactory) {

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

  @RequestMapping("/client/v1/on_search")
  @ResponseBody
  fun onSearchV1(
    @RequestParam messageId: String
  ): ResponseEntity<out ClientResponse> = onPoll(messageId)
}