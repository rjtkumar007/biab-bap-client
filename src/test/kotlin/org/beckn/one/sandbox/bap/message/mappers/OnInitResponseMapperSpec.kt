package org.beckn.one.sandbox.bap.message.mappers

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.message.entities.OnInit
import org.beckn.one.sandbox.bap.message.entities.OnInitMessage
import org.beckn.one.sandbox.bap.message.factories.ProtocolContextFactory
import org.beckn.one.sandbox.bap.message.factories.ProtocolOnInitMessageInitializedFactory
import org.beckn.one.sandbox.bap.schemas.ProtocolOnInit
import org.beckn.one.sandbox.bap.schemas.ProtocolOnInitMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class OnInitResponseMapperSpec @Autowired constructor(
  private val mapper: OnInitResponseMapper
) : DescribeSpec() {

  private val protocolResponse = ProtocolOnInit(
    context = ProtocolContextFactory.fixed,
    message = ProtocolOnInitMessage(
      ProtocolOnInitMessageInitializedFactory.create(1, 2)
    )
  )

  private val entityResponse = OnInit(
    context = ProtocolContextFactory.fixedAsEntity(protocolResponse.context),
    message = OnInitMessage(
      initialized = ProtocolOnInitMessageInitializedFactory.createAsEntity(protocolResponse.message?.initialized)
    )
  )

  init {
    describe("OnSelectResponseMapper") {
      it("should map properties from entity to schema") {
        mapper.protocolToEntity(protocolResponse) shouldBe entityResponse
      }

      it("should map properties from schema to entity") {
        mapper.entityToProtocol(entityResponse) shouldBe protocolResponse
      }
    }
  }
}