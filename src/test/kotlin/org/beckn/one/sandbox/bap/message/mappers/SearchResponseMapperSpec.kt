package org.beckn.one.sandbox.bap.message.mappers

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.message.entities.*
import org.beckn.one.sandbox.bap.message.factories.CatalogFactory
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponse
import org.beckn.one.sandbox.bap.schemas.ProtocolSearchResponseMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class SearchResponseMapperSpec @Autowired constructor(
  private val mapper: SearchResponseMapper
) : DescribeSpec() {
  val catalogFactory = CatalogFactory()

  private val fixedClock = Clock.fixed(
    Instant.parse("2018-11-30T18:35:24.00Z"),
    ZoneId.of("Asia/Calcutta")
  )

  init {
    describe("SearchResponseMapper") {
      it("should map all fields from schema to entity") {
        val protocolSearchResponse = ProtocolSearchResponse(
          message = ProtocolSearchResponseMessage(
            catalogFactory.create(1)
          ),
          context = org.beckn.one.sandbox.bap.schemas.Context(
            domain = "LocalRetail",
            country = "IN",
            action = org.beckn.one.sandbox.bap.schemas.Context.Action.SEARCH,
            city = "Pune",
            coreVersion = "0.9.1-draft03",
            bapId = "http://host.bap.com",
            bapUri = "http://host.bap.com",
            transactionId = "222",
            messageId = "222",
            timestamp = LocalDateTime.now(fixedClock)
          )
        )
        val mappedEntity = mapper.schemaToEntity(protocolSearchResponse)

        mappedEntity shouldBe SearchResponse(
          message = SearchResponseMessage(
            catalog = Catalog(
              bppProviders = listOf(
                ProviderCatalog(
                  id = "provider-1",
                  descriptor = descriptor("Retail-provider", 1),
                  categories = listOf(
                    Category(
                      id = "provider-1-category-1",
                      descriptor = descriptor("provider-1-category", 1),
                      tags = mapOf("category-tag1" to "category-value1")
                    )
                  ),
                  items = listOf(
                    Item(
                      id = "Item_1",
                      descriptor = descriptor("provider-1-item", 1),
                      price = Price(
                        currency = "Rupees",
                        value = "99",
                        minimumValue = "100",
                        estimatedValue = "101",
                        computedValue = "102",
                        offeredValue = "103",
                        listedValue = "104",
                        maximumValue = "105",
                      ),
                      categoryId = "provider-1-category-1",
                      tags = mapOf("item-tag1" to "item-value1"),
                      matched = true,
                      related = true,
                      recommended = true
                    )
                  )
                )
              )
            )
          ),
          context = Context(
            domain = "LocalRetail",
            country = "IN",
            action = Context.Action.SEARCH,
            city = "Pune",
            coreVersion = "0.9.1-draft03",
            bapId = "http://host.bap.com",
            bapUri = "http://host.bap.com",
            transactionId = "222",
            messageId = "222",
            timestamp = LocalDateTime.now(fixedClock)
          )
        )
      }
    }
  }

  fun descriptor(type: String, index: Int) = Descriptor(
    name = "$type-$index name",
    code = "$type-$index code",
    symbol = "$type-$index symbol",
    shortDesc = "A short description about $type-$index",
    longDesc = "A long description about $type-$index",
    images = listOf("uri:https://$type-$index-image-1.com", "uri:https://$type-$index-image-2.com"),
    audio = "$type-$index-image-audio-file-path",
    threeDRender = "$type-$index-3d"
  )
}