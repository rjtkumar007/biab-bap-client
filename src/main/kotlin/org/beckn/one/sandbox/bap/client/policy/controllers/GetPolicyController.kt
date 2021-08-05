package org.beckn.one.sandbox.bap.client.policy.controllers

import org.beckn.one.sandbox.bap.client.policy.services.GetPolicyService
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyResponseMessage
import org.beckn.one.sandbox.bap.client.shared.dtos.GetOrderPolicyDto
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.protocol.schemas.ProtocolContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
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

  @PostMapping("/client/v1/get_order_policy")
  @ResponseBody
  fun getOrderPolicyV1(@RequestBody request: GetOrderPolicyDto): ResponseEntity<ClientOrderPolicyResponse> {
    log.info("Got request for getting order policy from BPP")
    val context = getContext(request.context.transactionId, request.context.bppId)
    return getPolicyService.getCancellationPolicy(context = context).fold(
      {
        log.error("Error when getting order policy from BPP: {}", it)
        ResponseEntity.status(it.status())
          .body(ClientOrderPolicyResponse(context = context, error = it.error()))
      },
      {
        log.info("Successfully got order policy from BPP. Message: {}", it)
        ResponseEntity.ok(
          ClientOrderPolicyResponse(
            context = context,
            message = ClientOrderPolicyResponseMessage(cancellationPolicies = it)
          )
        )
      }
    )
  }

  private fun getContext(transactionId: String, bppId: String? = null) =
    contextFactory.create(action = ProtocolContext.Action.CANCEL, transactionId = transactionId, bppId = bppId)
}