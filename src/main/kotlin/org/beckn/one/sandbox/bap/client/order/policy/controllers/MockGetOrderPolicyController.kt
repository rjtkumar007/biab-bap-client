package org.beckn.one.sandbox.bap.client.order.policy.controllers

import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyResponseMessage
import org.beckn.one.sandbox.bap.client.shared.dtos.GetOrderPolicyDto
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.protocol.schemas.ProtocolDescriptor
import org.beckn.protocol.schemas.ProtocolOption
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class MockGetOrderPolicyController @Autowired constructor(
  private val contextFactory: ContextFactory
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v0/get_order_policy")
  @ResponseBody
  fun getOrderPolicy(@RequestBody request: GetOrderPolicyDto): ResponseEntity<ClientOrderPolicyResponse> {
    log.info("Got request for mock get order policy")
    return ResponseEntity.ok(
      ClientOrderPolicyResponse(
        context = contextFactory.create(
          messageId = "c6036d04-55e6-4e2e-8d31-24183a9f3ee8",
          transactionId = request.context.transactionId,
          bppId = request.context.bppId,
        ),
        message = ClientOrderPolicyResponseMessage(
          cancellationReasons = listOf(
            ProtocolOption("1", cancellationPolicy()),
            ProtocolOption("2", returnPolicy()),
          )
        )
      )
    )
  }

  private fun cancellationPolicy() = ProtocolDescriptor(
    name = "Cancellation Policy",
    code = "Cancellable within a day",
    shortDesc = "This item is cancellable with a day of the order being placed.",
    longDesc = "However if there is a delay in delivery and you would like to cancel the order after a day of placing the order then please contact the customer support.",
  )

  private fun returnPolicy() = ProtocolDescriptor(
    name = "Return Policy",
    code = "Non-Returnable",
    shortDesc = "This item is non-returnable due to the consumable nature of the product.",
    longDesc = "However, in the unlikely event of damaged, defective or different/wrong item delivered to you, we will provide a full refund or free replacement as applicable. We may contact you to ascertain the damage or defect in the product prior to issuing refund/replacement.",
  )

}