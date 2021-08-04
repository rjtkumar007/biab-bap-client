package org.beckn.one.sandbox.bap.client.fulfillment.track.services

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientContext
import org.beckn.one.sandbox.bap.client.shared.dtos.TrackRequestDto
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.protocol.schemas.ProtocolTrackRequestMessage
import org.mockito.Mockito.mock
import org.mockito.kotlin.verifyNoMoreInteractions

class TrackServiceSpec : DescribeSpec() {
  private val context = ContextFactoryInstance.create().create()
  private val registryService = mock(RegistryService::class.java)
  private val bppTrackService = mock(BppTrackService::class.java)
  private val trackService = TrackService(
    registryService = registryService,
    bppTrackService = bppTrackService,
  )

  init {
    describe("Track") {
      it("should validate that bpp id is not null") {
        val trackResponse = trackService.track(
          context = context,
          request = TrackRequestDto(
            context = ClientContext(transactionId = UuidFactory().create(), bppId = null),
            message = ProtocolTrackRequestMessage(orderId = "Order Id 1")
          )
        )

        trackResponse shouldBeLeft BppError.BppIdNotPresent
        verifyNoMoreInteractions(registryService)
        verifyNoMoreInteractions(bppTrackService)
      }
    }
  }
}