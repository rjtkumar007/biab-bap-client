package org.beckn.one.sandbox.bap.client.order.init.services

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import org.beckn.one.sandbox.bap.client.external.hasBody
import org.beckn.one.sandbox.bap.client.external.isAckNegative
import org.beckn.one.sandbox.bap.client.external.isInternalServerError
import org.beckn.one.sandbox.bap.client.external.provider.BppClient
import org.beckn.one.sandbox.bap.client.external.provider.BppClientFactory
import org.beckn.one.sandbox.bap.client.shared.dtos.OrderDto
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.protocol.schemas.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import retrofit2.Response

@Service
class BppInitService @Autowired constructor(
  private val bppServiceClientFactory: BppClientFactory
) {
  private val log: Logger = LoggerFactory.getLogger(BppInitService::class.java)

  fun init(context: ProtocolContext, bppUri: String, order: OrderDto): Either<BppError, ProtocolAckResponse> {
    return Either.catch {
      log.info("Invoking Init API on BPP: {}", bppUri)
      val bppServiceClient = bppServiceClientFactory.getClient(bppUri)
      val httpResponse =
        invokeBppInitApi(
          bppServiceClient = bppServiceClient,
          context = context,
          order = order
        )
      log.info("BPP init API response. Status: {}, Body: {}", httpResponse.code(), httpResponse.body())
      return when {
        httpResponse.isInternalServerError() -> Left(BppError.Internal)
        !httpResponse.hasBody() -> Left(BppError.NullResponse)
        httpResponse.isAckNegative() -> Left(BppError.Nack)
        else -> Right(httpResponse.body()!!)
      }
    }.mapLeft {
      log.error("Error when initiating init", it)
      BppError.Internal
    }
  }

  private fun invokeBppInitApi(
    bppServiceClient: BppClient,
    context: ProtocolContext,
    order: OrderDto
  ): Response<ProtocolAckResponse> {
    val initRequest = ProtocolInitRequest(
      context = context,
      ProtocolInitRequestMessage(
        order = ProtocolOrder(
          provider = order?.items?.first()?.provider?.let {
            ProtocolSelectMessageSelectedProvider(
              id = it?.id,
              locations = listOf(ProtocolSelectMessageSelectedProviderLocations(id = it?.locations?.first() ?: "" ))
            )
          },
          items = order.items!!.map { ProtocolSelectMessageSelectedItems(id = it.id, quantity = it.quantity) },
          billing = order.billingInfo,
          fulfillment = ProtocolFulfillment(
            end = ProtocolFulfillmentEnd(
              contact = ProtocolContact(
                phone = order.deliveryInfo.phone,
                email = order.deliveryInfo.email
              ), location = order.deliveryInfo.location
            ),
            type = order.deliveryInfo.type,
            customer = ProtocolCustomer(ProtocolPerson(name = order.deliveryInfo.name)),
            provider_id = order.items?.first()?.provider?.id
          ),
          addOns = emptyList(),
          offers = emptyList(),
        )
      )
    )
    return bppServiceClient.init(initRequest).execute()
  }
}