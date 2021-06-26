package org.beckn.one.sandbox.bap.client.services

import io.kotest.assertions.arrow.either.shouldBeRight
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.dtos.ClientSearchResponse
import org.beckn.one.sandbox.bap.client.dtos.ClientSearchResponseMessage
import org.beckn.one.sandbox.bap.message.factories.CatalogFactory
import org.beckn.one.sandbox.bap.schemas.ProtocolContext
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponseMessage
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

internal class GenericOnReplyTransformerSpec : DescribeSpec() {
  private val fixedClock = Clock.fixed(
    Instant.parse("2018-11-30T18:35:24.00Z"),
    ZoneId.of("Asia/Calcutta")
  )
  private val context = ProtocolContext(
    domain = "LocalRetail",
    country = "IN",
    action = ProtocolContext.Action.SEARCH,
    city = "Pune",
    coreVersion = "0.9.1-draft03",
    bapId = "http://host.bap.com",
    bapUri = "http://host.bap.com",
    transactionId = "222",
    messageId = "222",
    timestamp = LocalDateTime.now(fixedClock)
  )
  private val catalog1 = CatalogFactory.create(1)
  private val catalog2 = CatalogFactory.create(2)
  private val protocolSearchResponse1 = ProtocolSearchResponse(
    message = ProtocolSearchResponseMessage(
      catalog1,
    ),
    context = context
  )
  private val protocolSearchResponse2 = ProtocolSearchResponse(
    message = ProtocolSearchResponseMessage(
      catalog2,
    ),
    context = context
  )

  init {
    describe("GenericOnReplyTransformerSpec.forSearchResults") {
      context("when invoked") {
        val clientSearchResponse = GenericOnPollTransformer.forSearchResults
          .transform(listOf(protocolSearchResponse1, protocolSearchResponse2), context)

        it("should transform the protocol response to client response") {
          clientSearchResponse shouldBeRight ClientSearchResponse(
            context = context,
            message = ClientSearchResponseMessage(
              catalogs = listOf(catalog1, catalog2)
            )
          )
        }
      }
    }
  }
}