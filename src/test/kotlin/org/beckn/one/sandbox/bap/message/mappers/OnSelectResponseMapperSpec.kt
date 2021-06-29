package org.beckn.one.sandbox.bap.message.mappers

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.message.entities.OnSelect
import org.beckn.one.sandbox.bap.message.entities.OnSelectMessage
import org.beckn.one.sandbox.bap.message.factories.ProtocolContextFactory
import org.beckn.one.sandbox.bap.message.factories.ProtocolOnSelectMessageSelectedFactory
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSelect
import org.beckn.one.sandbox.bap.schemas.ProtocolOnSelectMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class OnSelectResponseMapperSpec @Autowired constructor(
  private val mapper: OnSelectResponseMapper
): DescribeSpec() {

  private val protocolResponse = ProtocolOnSelect(
    context = ProtocolContextFactory.fixed,
    message = ProtocolOnSelectMessage(
      ProtocolOnSelectMessageSelectedFactory.create(1, 2)
    )
  )
  init {
      describe("OnSelectResponseMapper") {
        it("should map properties from entity to schema") {
          mapper.protocolToEntity(protocolResponse) shouldBe OnSelect(
            context = ProtocolContextFactory.fixedAsEntity(protocolResponse.context),
            message = OnSelectMessage(
              ProtocolOnSelectMessageSelectedFactory.createAsEntity(protocolResponse.message?.selected)
            )
          )
        }
      }
  }
}