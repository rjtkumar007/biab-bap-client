package org.beckn.one.sandbox.bap.client.rating.services

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientContext
import org.beckn.one.sandbox.bap.client.shared.dtos.RatingRequestDto
import org.beckn.one.sandbox.bap.client.shared.dtos.RatingRequestMessage
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.mockito.Mockito.mock
import org.mockito.kotlin.verifyNoMoreInteractions

class RatingServiceSpec : DescribeSpec() {
  private val context = ContextFactoryInstance.create().create()
  private val registryService = mock(RegistryService::class.java)
  private val bppRatingService = mock(BppRatingService::class.java)
  private val ratingService = RatingService(registryService = registryService, bppRatingService = bppRatingService)

  init {
    describe("Rating") {
      it("should validate that bpp id is not null") {
        val ratingResponse = ratingService.rating(
          context = context,
          request = RatingRequestDto(
            context = ClientContext(transactionId = UuidFactory().create(), bppId = null),
            message = RatingRequestMessage(refId = "item id 1", value = 5)
          )
        )

        ratingResponse shouldBeLeft BppError.BppIdNotPresent
        verifyNoMoreInteractions(registryService)
        verifyNoMoreInteractions(bppRatingService)
      }
    }
  }
}