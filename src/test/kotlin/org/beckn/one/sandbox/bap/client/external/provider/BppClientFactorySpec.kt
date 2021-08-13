package org.beckn.one.sandbox.bap.client.external.provider

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@ActiveProfiles(value = ["cached-enabled"])
@TestPropertySource(locations = ["/application-test.yml"])
class BppClientFactorySpec @Autowired constructor(
  private val bppClientFactory: BppClientFactory
) : DescribeSpec() {
  init {
    describe("Get Client Caching") {

      it("should not create a client instance if it exists") {
        val bppClientFirstInstance = bppClientFactory.getClient("http://bpp.com")
        val bppClientSecondInstance = bppClientFactory.getClient("http://bpp.com")
        val anotherBppClientFirstInstance = bppClientFactory.getClient("http://another-bpp.com")

        bppClientFirstInstance shouldBeSameInstanceAs bppClientSecondInstance
        bppClientFirstInstance shouldNotBeSameInstanceAs anotherBppClientFirstInstance
        bppClientSecondInstance shouldNotBeSameInstanceAs anotherBppClientFirstInstance
        anotherBppClientFirstInstance shouldNotBe null
      }
    }
  }
}