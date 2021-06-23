package org.beckn.one.sandbox.bap.message.mappers

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
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

        mappedEntity.bppProviders shouldNotBe null
        mappedEntity.bppProviders?.size shouldBe 1
        mappedEntity.bppProviders?.first()?.id shouldBe catalog1Schema.bppProviders?.first()?.id
        mappedEntity.bppProviders?.first()?.items?.size shouldBe 1
        mappedEntity.bppProviders?.first()?.items?.first()?.id shouldBe catalog1Schema.bppProviders?.first()?.items?.first()?.id
      }
    }
  }
}