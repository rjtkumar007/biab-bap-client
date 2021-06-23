package org.beckn.one.sandbox.bap.message.mappers

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.message.entities.*
import org.beckn.one.sandbox.bap.message.factories.CatalogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class CatalogMapperSpec @Autowired constructor(
  private val mapper: CatalogMapper
) : DescribeSpec() {
  val catalogFactory = CatalogFactory()

  init {
    describe("SearchResponseMapper") {
      it("should map all fields from schema to entity") {
        val catalog1Schema = catalogFactory.create()

        val mappedEntity = mapper.schemaToEntity(catalog1Schema)

        mappedEntity shouldBe
            Catalog(
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