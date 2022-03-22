package org.beckn.one.sandbox.bap.client.policy.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolOption
import org.beckn.protocol.schemas.ProtocolRatingCategory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class GetPolicyService @Autowired constructor(
  private val bppService: BppPolicyService,
  private val registryService: RegistryService,
  private val log: Logger = LoggerFactory.getLogger(GetPolicyService::class.java)
) {
  fun getCancellationPolicy(context: ProtocolContext): Either<HttpError, ProtocolAckResponse> {
    log.info("Got a get cancellation policy request.  Context: {}", context)

    if (context.bppId == null) {
      log.info("BPPId not present")
      return Either.Left(BppError.BppIdNotPresent)
    }

    return registryService.lookupBppById(context.bppId!!)
      .flatMap {
        bppService.getCancellationReasons(
          bppUri = it.first().subscriber_url,
          context = context
        )
      }
  }

  fun getRatingCategoriesPolicy(context: ProtocolContext): Either<HttpError, List<ProtocolRatingCategory>> {
    log.info("Got a get rating category policy request.  Context: {}", context)

    if (context.bppId == null) {
      log.info("BPPId not present")
      return Either.Left(BppError.BppIdNotPresent)
    }

    return registryService.lookupBppById(context.bppId!!)
      .flatMap {
        bppService.getRatingCategories(
          bppUri = it.first().subscriber_url,
          context = context
        )
      }
  }
}