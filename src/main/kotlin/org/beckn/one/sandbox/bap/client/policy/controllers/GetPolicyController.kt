package org.beckn.one.sandbox.bap.client.policy.controllers

import org.beckn.one.sandbox.bap.client.policy.services.GetPolicyService
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyMultipleResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyResponseMessage
import org.beckn.one.sandbox.bap.client.shared.dtos.GetOrderPolicyDto
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ResponseMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class GetPolicyController @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val getPolicyService: GetPolicyService
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/get_cancellation_policy")
  @ResponseBody
  fun getCancellationPolicyV1(@RequestBody request: GetOrderPolicyDto): ResponseEntity<ProtocolAckResponse> {
    log.info("Got request for getting order policy from BPP")
    val context = getContext(request.context.transactionId, request.context.bppId, ProtocolContext.Action.SEARCH)
    return getPolicyService.getCancellationPolicy(context = context).fold(
      {
        log.error("Error when getting order policy from BPP: {}", it)
        ResponseEntity.status(it.status())
          .body(ProtocolAckResponse(context = context, error = it.error(),message = ResponseMessage.nack()))
      },
      {
        log.info("Successfully got order policy from BPP. Message: {}", it)
        ResponseEntity.ok(ProtocolAckResponse(context = context, message = ResponseMessage.ack()))
      }
    )
  }

  @PostMapping("/client/v1/get_rating_category")
  @ResponseBody
  fun getRatingCategoriesV1(@RequestBody request: GetOrderPolicyDto): ResponseEntity<ClientOrderPolicyResponse> {
    log.info("Got request for getting rating categories from BPP")
    val context = getContext(request.context.transactionId, request.context.bppId, ProtocolContext.Action.SEARCH)
    return getPolicyService.getRatingCategoriesPolicy(context = context).fold(
      {
        log.error("Error when getting rating categories from BPP: {}", it)
        ResponseEntity.status(it.status())
          .body(ClientOrderPolicyResponse(context = context, error = it.error()))
      },
      {
        log.info("Successfully got rating categories from BPP. Message: {}", it)
        ResponseEntity.ok(
          ClientOrderPolicyResponse(
            context = context,
            message = ClientOrderPolicyResponseMessage(ratingCategories = it)
          )
        )
      }
    )
  }

//  @PostMapping("/client/v1/get_order_policy")
//  @ResponseBody
//  fun getOrderPolicyV1(@RequestBody request: GetOrderPolicyDto): ResponseEntity<ClientOrderPolicyMultipleResponse> {
//    log.info("Got request for getting all order policies from BPP")
//    val context = getContext(request.context.transactionId, request.context.bppId, ProtocolContext.Action.SEARCH)
//    var status = mutableListOf<HttpStatus>()
//    val response = ClientOrderPolicyMultipleResponse(context = context, error = mutableListOf())
//    getPolicyService.getRatingCategoriesPolicy(context = context).fold(
//      { it ->
//        log.error("Error when getting rating categories from BPP: {}", it)
//        status.add(it.status())
//        it.error().also { response.error!!.add(it) }
//      },
//      {
//        log.info("Successfully got rating categories from BPP. Message: {}", it)
//        response.message = ClientOrderPolicyResponseMessage(ratingCategories = it)
//      }
//    )
//    getPolicyService.getCancellationPolicy(context = context).fold(
//      { it ->
//        log.error("Error when getting order policy from BPP: {}", it)
//        status.add(it.status())
//        it.error().also { response.error!!.add(it) }
//      },
//      {
//        log.info("Successfully got order policy from BPP. Message: {}", it)
//        if (response.message != null && response.message!!.ratingCategories != null)
//          response.message = ClientOrderPolicyResponseMessage(
//            cancellationReasons = it.message.ack,
//            ratingCategories = response.message!!.ratingCategories
//          )
//        else
//          response.message = ClientOrderPolicyResponseMessage(cancellationReasons = it)
//      }
//    )
//    if (response.message != null)
//      return ResponseEntity.ok(response)
//    return ResponseEntity.status(status.first().value()).body(response)
//  }

  private fun getContext(transactionId: String, bppId: String? = null, action: ProtocolContext.Action) =
    contextFactory.create(action = action, transactionId = transactionId, bppId = bppId)
}