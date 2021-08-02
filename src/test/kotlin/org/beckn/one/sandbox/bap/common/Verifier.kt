package org.beckn.one.sandbox.bap.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.external.domains.Subscriber
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberLookupRequest
import org.beckn.protocol.schemas.ProtocolAckResponse
import org.beckn.protocol.schemas.ProtocolContext
import org.beckn.protocol.schemas.ProtocolError
import org.beckn.protocol.schemas.ResponseMessage

class Verifier(
  val objectMapper: ObjectMapper
) {

  fun verifyResponseMessage(
    responseString: String,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null,
    action: ProtocolContext.Action
  ): ProtocolAckResponse {
    val getQuoteResponse = objectMapper.readValue(responseString, ProtocolAckResponse::class.java)
    getQuoteResponse.context shouldNotBe null
    getQuoteResponse.context?.messageId shouldNotBe null
    getQuoteResponse.context?.action shouldBe action
    getQuoteResponse.message shouldBe expectedMessage
    getQuoteResponse.error shouldBe expectedError
    return getQuoteResponse
  }

  fun verifyThatSubscriberLookupApiWasInvoked(
    registryBppLookupApi: WireMockServer,
    bppApi: WireMockServer
  ) {
    registryBppLookupApi.verify(
      postRequestedFor(urlEqualTo("/lookup"))
        .withRequestBody(
          equalToJson(
            objectMapper.writeValueAsString(
              SubscriberLookupRequest(
                subscriber_id = bppApi.baseUrl(),
                type = Subscriber.Type.BPP,
                domain = Domain.LocalRetail.value,
                country = Country.India.value,
                city = City.Bengaluru.value
              )
            )
          )
        )
    )
  }

}