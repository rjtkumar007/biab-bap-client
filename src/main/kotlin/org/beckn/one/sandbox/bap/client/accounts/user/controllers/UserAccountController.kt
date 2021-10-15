package org.beckn.one.sandbox.bap.client.accounts.user.controllers

import org.beckn.one.sandbox.bap.client.accounts.user.services.UserAccountServices
import org.beckn.one.sandbox.bap.errors.HttpError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.message.entities.UserDao
import org.beckn.protocol.schemas.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserAccountController @Autowired constructor(
  private val contextFactory: ContextFactory,
  private val userAccountServices: UserAccountServices
) {
  val log: Logger = LoggerFactory.getLogger(this::class.java)

  @PostMapping("/client/v1/account_details")
  @ResponseBody
  fun addUserAccount(@RequestBody request: UserDao): ResponseEntity<ProtocolAckResponse> {
    return userAccountServices.findById(request.userId!!).fold(
      {
        log.error("Error when finding user: {}", it)
        mapToErrorResponse(it)
      }, {
        updateUserAccount(request)
      }
    )
  }

  private fun updateUserAccount(request: UserDao) : ResponseEntity<ProtocolAckResponse>  {
      log.error("User already exist: {}")
      return userAccountServices.updateUserById(
        request
      ).fold(
        {
          log.error("Error when updating user: {}", it)
          mapToErrorResponse(it)
        },
        {
          log.info("Successfully updated  user : {}", it)
          ResponseEntity.ok(ProtocolAckResponse(null, message = ResponseMessage.ack(), error = null))
        }
      )
    }



  private fun mapToErrorResponse(it: HttpError) = ResponseEntity
    .status(it.status())
    .body(
      ProtocolAckResponse(
        context = null,
        message = it.message(),
        error = it.error()
      )
    )

  private fun getContext(transactionId: String, bppId: String? = null) =
    contextFactory.create(action = ProtocolContext.Action.CANCEL, transactionId = transactionId, bppId = bppId)
}