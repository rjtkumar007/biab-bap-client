package org.beckn.one.sandbox.bap.client.policy.services

import io.kotest.assertions.arrow.either.shouldBeLeft
import io.kotest.core.spec.style.DescribeSpec
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.common.factories.ContextFactoryInstance
import org.mockito.Mockito
import org.mockito.kotlin.verifyNoMoreInteractions

class GetPolicyServiceSpec : DescribeSpec() {
  private val context = ContextFactoryInstance.create().create()
  private val registryService = Mockito.mock(RegistryService::class.java)
  private val bppPolicyService = Mockito.mock(BppPolicyService::class.java)
  private val getPolicyService = GetPolicyService(
    registryService = registryService,
    bppService = bppPolicyService
  )

  init{
    describe("Get cancellation policy") {
      val cancellationResponse = getPolicyService.getCancellationPolicy(context)

      cancellationResponse shouldBeLeft BppError.BppIdNotPresent
      verifyNoMoreInteractions(registryService)
      verifyNoMoreInteractions(bppPolicyService)
    }

    describe("Get rating categories policy") {
      val ratingCategoriesResponse = getPolicyService.getRatingCategoriesPolicy(context)

      ratingCategoriesResponse shouldBeLeft BppError.BppIdNotPresent
      verifyNoMoreInteractions(registryService)
      verifyNoMoreInteractions(bppPolicyService)
    }
  }
}