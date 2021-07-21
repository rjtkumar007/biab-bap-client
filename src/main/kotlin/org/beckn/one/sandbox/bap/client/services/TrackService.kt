package org.beckn.one.sandbox.bap.client.services

import arrow.core.Either
import arrow.core.flatMap
import org.beckn.one.sandbox.bap.client.dtos.TrackRequestDto
import org.beckn.one.sandbox.bap.client.errors.bpp.BppError
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.message.entities.MessageDao
import org.beckn.protocol.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TrackService @Autowired constructor(
  private val registryService: RegistryService,
  private val bppService: BppService,
) {
  private val log: Logger = LoggerFactory.getLogger(QuoteService::class.java)

  fun track(context: ProtocolContext, request: TrackRequestDto): Either<HttpError, MessageDao?> {
    log.info("Got track request. Request: {}", request)
    registryService.lookupBppById(request.context.bppId!!)
      .flatMap { Either.Right(it.first()) }
      .flatMap {
        bppService.track(it.subscriber_url, context, request)
      }
    return Either.Left(BppError.Internal)
  }

}
