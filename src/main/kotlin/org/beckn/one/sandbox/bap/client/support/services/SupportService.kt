package org.beckn.one.sandbox.bap.client.support.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.shared.dtos.SupportRequestMessage
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SupportService @Autowired constructor(
  private val bppSupportService: BppSupportService,
  private val registryService: RegistryService,
  private val log: Logger = LoggerFactory.getLogger(SupportService::class.java)
) {
  fun getSupport(context: ProtocolContext, supportRequestMessage: SupportRequestMessage, bppId: String?):
      Either<HttpError, ProtocolAckResponse?> {
    log.info("Got support request for reference Id: {}", supportRequestMessage.refId)

    if (bppId == null) {
      log.info("BPPId not present")
      return Either.Left(BppError.BppIdNotPresent)
    }

    return registryService.lookupBppById(bppId)
      .flatMap {
        bppSupportService.support(
          bppUri = it.first().subscriber_url,
          context = context,
          refId = supportRequestMessage.refId
        )
      }
  }
}
