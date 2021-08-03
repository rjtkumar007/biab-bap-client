package org.beckn.one.sandbox.bap.client.rating.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.shared.dtos.RatingRequestDto
import org.beckn.one.sandbox.bap.client.shared.services.RegistryService
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RatingService @Autowired constructor(
  private val bppRatingService: BppRatingService,
  private val registryService: RegistryService,
  private val log: Logger = LoggerFactory.getLogger(RatingService::class.java)
) {

  fun rating(
    context: ProtocolContext,
    request: RatingRequestDto
  ): Either<HttpError, ProtocolAckResponse?> {
    log.info("Got rating request for Id: {}", request.message.refId)
    return request.validate()
      .flatMap { registryService.lookupBppById(it.context.bppId!!) }
      .flatMap {
        bppRatingService.rating(
          bppUri = it.first().subscriber_url,
          context = context,
          refId = request.message.refId,
          value = request.message.value
        )
      }
  }
}
