package org.beckn.one.sandbox.bap.message.mappers

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.message.entities.Catalog
import org.beckn.one.sandbox.bap.message.entities.Item
import org.beckn.one.sandbox.bap.message.entities.ProviderCatalog
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

        mappedEntity shouldBe Catalog(
          listOf(
            ProviderCatalog(
              id = "provider-1.com",
              items = listOf(Item(id = "Item_1"))
            )
          )
        )
      }
    }
  }
}