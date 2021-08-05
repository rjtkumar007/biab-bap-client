package org.beckn.one.sandbox.bap.client.policy.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.client.external.domains.Subscriber
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberLookupRequest
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientContext
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyResponse
import org.beckn.one.sandbox.bap.client.shared.dtos.ClientOrderPolicyResponseMessage
import org.beckn.one.sandbox.bap.client.shared.dtos.GetOrderPolicyDto
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.common.City
import org.beckn.one.sandbox.bap.common.Country
import org.beckn.one.sandbox.bap.common.Domain
import org.beckn.one.sandbox.bap.common.factories.MockNetwork
import org.beckn.one.sandbox.bap.common.factories.SubscriberDtoFactory
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.protocol.schemas.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class GetPolicyControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val contextFactory: ContextFactory,
  val uuidFactory: UuidFactory
) : DescribeSpec() {
  init {
    describe("Get order cancellation policies") {
      MockNetwork.startAllSubscribers()

      val context =
        ClientContext(transactionId = uuidFactory.create(), bppId = MockNetwork.retailBengaluruBpp.baseUrl())
      val getOrderPolicyDto = GetOrderPolicyDto(context = context)

      beforeEach {
        MockNetwork.resetAllSubscribers()
        stubBppLookupApi(MockNetwork.registryBppLookupApi, MockNetwork.retailBengaluruBpp)
      }

      it("Should return error when BPP get cancellation reasons call fails") {
        MockNetwork.retailBengaluruBpp.stubFor(
          WireMock.post("/get_cancellation_reasons").willReturn(WireMock.serverError())
        )

        val getOrderPolicyResponseString =
          invokeGetOrderPolicy(getOrderPolicyDto).andExpect(MockMvcResultMatchers.status().isInternalServerError)
            .andReturn().response.contentAsString

        val getOrderPolicyResponse =
          verifyGetOrderPolicyResponseMessage(
            getOrderPolicyResponseString,
            getOrderPolicyDto,
            null,
            BppError.Internal.error()
          )

        verifyThatBppGetCancellationReasonsApiWasInvoked(
          getOrderPolicyResponse,
          MockNetwork.retailBengaluruBpp
        )
        verifyThatSubscriberLookupApiWasInvoked(MockNetwork.registryBppLookupApi, MockNetwork.retailBengaluruBpp)
      }

      it("should invoke BPP get cancellation reasons api and save message") {
        MockNetwork.retailBengaluruBpp
          .stubFor(
            WireMock.post("/get_cancellation_reasons").willReturn(
              WireMock.okJson(
                objectMapper.writeValueAsString(
                  listOf(
                    ProtocolOption(
                      descriptor = ProtocolDescriptor(
                        name = "No Longer Required",
                        code = "2"
                      ),
                      id = ".retail.kiranaind.blr2@mandi.succinct.in.cancellation_reason"
                    )
                  )
                )
              )
            )
          )

        val getOrderPolicyResponseString = invokeGetOrderPolicy(getOrderPolicyDto)
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val getOrderPolicyResponse = verifyGetOrderPolicyResponseMessage(
          getOrderPolicyResponseString,
          getOrderPolicyDto,
          ClientOrderPolicyResponseMessage(
            cancellationPolicies = listOf(
              ProtocolOption(
                descriptor = ProtocolDescriptor(
                  name = "No Longer Required",
                  code = "2"
                ),
                id = ".retail.kiranaind.blr2@mandi.succinct.in.cancellation_reason"
              )
            )
          )
        )

        verifyThatBppGetCancellationReasonsApiWasInvoked(getOrderPolicyResponse, MockNetwork.retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasInvoked(MockNetwork.registryBppLookupApi, MockNetwork.retailBengaluruBpp)
      }

      MockNetwork.registryBppLookupApi.stop()
    }
  }

  private fun verifyThatSubscriberLookupApiWasInvoked(
    registryBppLookupApi: WireMockServer,
    bppApi: WireMockServer
  ) {
    registryBppLookupApi.verify(
      WireMock.postRequestedFor(WireMock.urlEqualTo("/lookup"))
        .withRequestBody(
          WireMock.equalToJson(
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

  private fun verifyThatBppGetCancellationReasonsApiWasInvoked(
    orderPolicyResponse: ClientOrderPolicyResponse,
    providerApi: WireMockServer
  ) {
    val protocolGetCancellationReasonsRequest = getProtocolGetCancellationReasonsRequest(orderPolicyResponse)
    providerApi.verify(
      WireMock.postRequestedFor(WireMock.urlEqualTo("/get_cancellation_reasons"))
        .withRequestBody(WireMock.equalToJson(objectMapper.writeValueAsString(protocolGetCancellationReasonsRequest)))
    )
  }

  private fun getProtocolGetCancellationReasonsRequest(
    orderPolicyResponse: ClientOrderPolicyResponse
  ): ProtocolGetCancellationReasonsRequest =
    ProtocolGetCancellationReasonsRequest(
      context = orderPolicyResponse.context
    )

  private fun verifyGetOrderPolicyResponseMessage(
    getOrderPolicyResponseString: String,
    getOrderPolicyDto: GetOrderPolicyDto,
    expectedMessage: ClientOrderPolicyResponseMessage?,
    expectedError: ProtocolError? = null
  ): ClientOrderPolicyResponse {
    val getOrderPolicyResponse =
      objectMapper.readValue(getOrderPolicyResponseString, ClientOrderPolicyResponse::class.java)
    getOrderPolicyResponse.context shouldNotBe null
    getOrderPolicyResponse.context.messageId shouldNotBe null
    getOrderPolicyResponse.context.transactionId shouldBe getOrderPolicyDto.context.transactionId
    getOrderPolicyResponse.context.action shouldBe ProtocolContext.Action.CANCEL
    getOrderPolicyResponse.message shouldBe expectedMessage
    getOrderPolicyResponse.error shouldBe expectedError
    return getOrderPolicyResponse
  }

  private fun invokeGetOrderPolicy(getOrderPolicyDto: GetOrderPolicyDto) = mockMvc.perform(
    MockMvcRequestBuilders.post("/client/v1/get_order_policy").header(
      org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
    ).content(objectMapper.writeValueAsString(getOrderPolicyDto))
  )

  private fun getSubscriberForBpp(bppApi: WireMockServer) =
    objectMapper.writeValueAsString(
      listOf(
        SubscriberDtoFactory.getDefault(
          subscriber_id = bppApi.baseUrl(),
          baseUrl = bppApi.baseUrl(),
          type = SubscriberDto.Type.BPP,
        )
      )
    )

  private fun stubBppLookupApi(
    registryBppLookupApi: WireMockServer,
    providerApi: WireMockServer
  ) {
    registryBppLookupApi
      .stubFor(
        WireMock.post("/lookup")
          .withRequestBody(WireMock.matchingJsonPath("$.subscriber_id", WireMock.equalTo(providerApi.baseUrl())))
          .willReturn(WireMock.okJson(getSubscriberForBpp(providerApi)))
      )
  }

}