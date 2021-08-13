package org.beckn.one.sandbox.bap.client.external.gateway

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@ActiveProfiles(value = ["cache-enabled"])
@TestPropertySource(locations = ["/application-test.yml"])
class GatewayClientFactorySpec @Autowired constructor(
  private val gatewayClientFactory: GatewayClientFactory
) : DescribeSpec() {
  init {
    describe("Get Client Caching") {

      it("should not create a client instance if it exists") {
        val gatewayClientFirstInstance = gatewayClientFactory.getClient("http://gateway.com")
        val gatewayClientSecondInstance = gatewayClientFactory.getClient("http://gateway.com")
        val anotherGatewayClientFirstInstance = gatewayClientFactory.getClient("http://another-gateway.com")

        gatewayClientFirstInstance shouldBeSameInstanceAs gatewayClientSecondInstance
        gatewayClientFirstInstance shouldNotBeSameInstanceAs anotherGatewayClientFirstInstance
        gatewayClientSecondInstance shouldNotBeSameInstanceAs anotherGatewayClientFirstInstance
        anotherGatewayClientFirstInstance shouldNotBe null
      }
    }
  }
}