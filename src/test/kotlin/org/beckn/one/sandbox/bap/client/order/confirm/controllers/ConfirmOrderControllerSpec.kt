package org.beckn.one.sandbox.bap.client.order.confirm.controllers

import arrow.core.Either
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beckn.one.sandbox.bap.auth.model.User
import org.beckn.one.sandbox.bap.client.external.domains.Subscriber
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberDto
import org.beckn.one.sandbox.bap.client.external.registry.SubscriberLookupRequest
import org.beckn.one.sandbox.bap.client.factories.OrderDtoFactory
import org.beckn.one.sandbox.bap.client.order.confirm.services.ConfirmOrderService
import org.beckn.one.sandbox.bap.client.order.status.controllers.OnOrderStatusPollController
import org.beckn.one.sandbox.bap.client.shared.dtos.*
import org.beckn.one.sandbox.bap.client.shared.errors.bpp.BppError
import org.beckn.one.sandbox.bap.client.shared.services.GenericOnPollService
import org.beckn.one.sandbox.bap.common.City
import org.beckn.one.sandbox.bap.common.Country
import org.beckn.one.sandbox.bap.common.Domain
import org.beckn.one.sandbox.bap.common.factories.MockNetwork
import org.beckn.one.sandbox.bap.common.factories.MockNetwork.anotherRetailBengaluruBpp
import org.beckn.one.sandbox.bap.common.factories.MockNetwork.registryBppLookupApi
import org.beckn.one.sandbox.bap.common.factories.MockNetwork.retailBengaluruBpp
import org.beckn.one.sandbox.bap.common.factories.ResponseFactory
import org.beckn.one.sandbox.bap.common.factories.SubscriberDtoFactory
import org.beckn.one.sandbox.bap.errors.database.DatabaseError
import org.beckn.one.sandbox.bap.factories.ContextFactory
import org.beckn.one.sandbox.bap.factories.UuidFactory
import org.beckn.one.sandbox.bap.message.entities.OrderDao
import org.beckn.one.sandbox.bap.message.services.ResponseStorageService
import org.beckn.protocol.schemas.*
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles(value = ["test"])
@TestPropertySource(locations = ["/application-test.yml"])
class ConfirmOrderControllerSpec @Autowired constructor(
  val mockMvc: MockMvc,
  val objectMapper: ObjectMapper,
  val contextFactory: ContextFactory,
  val uuidFactory: UuidFactory,
  private val confirmOrderService: ConfirmOrderService,
  ) : DescribeSpec() {
  init {
    describe("Confirm order with BPP") {
      MockNetwork.startAllSubscribers()
      val context = ClientContext(transactionId = uuidFactory.create())
      val orderRequest = OrderRequestDto(
        message = OrderDtoFactory.create(
          bpp1_id = retailBengaluruBpp.baseUrl(),
          provider1_id = "padma coffee works",
          payment = OrderPayment(100.0, status = OrderPayment.Status.PAID, transactionId = "abc")
        ), context = context
      )
      val orderRequestList = listOf(OrderRequestDto(
        message = OrderDtoFactory.create(
          bpp1_id = retailBengaluruBpp.baseUrl(),
          provider1_id = "padma coffee works",
          payment = OrderPayment(100.0, status = OrderPayment.Status.PAID, transactionId = "abc")
        ), context = context
      ))

      beforeEach {
        MockNetwork.resetAllSubscribers()
        stubBppLookupApi(registryBppLookupApi, retailBengaluruBpp)
        stubBppLookupApi(registryBppLookupApi, anotherRetailBengaluruBpp)
      }

      it("should return error when BPP confirm call fails") {
        retailBengaluruBpp.stubFor(post("/confirm").willReturn(serverError()))

        val confirmOrderResponseString =
          invokeConfirmOrder(orderRequest).andExpect(MockMvcResultMatchers.status().isInternalServerError)
            .andReturn().response.contentAsString

        val confirmOrderResponse =
          verifyConfirmResponseMessage(
            confirmOrderResponseString,
            orderRequest,
            ResponseMessage.nack(),
            ProtocolError("BAP_011", "BPP returned error")
          )
        verifyThatBppConfirmApiWasInvoked(confirmOrderResponse, orderRequest, retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, retailBengaluruBpp)
      }

      it("should validate that order contains items from only one bpp") {
        verifyThatBppConfirmApiWasNotInvoked(retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi)
        verifyThatSubscriberLookupApiWasNotInvoked(anotherRetailBengaluruBpp)
      }

      it("should validate that order contains items from only one provider") {
        verifyThatBppConfirmApiWasNotInvoked(retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi)
        verifyThatSubscriberLookupApiWasNotInvoked(anotherRetailBengaluruBpp)
      }

      it("should validate if payment is done") {
        val orderRequestForTest = OrderRequestDto(
          message = OrderDtoFactory.create(
            bpp1_id = retailBengaluruBpp.baseUrl(),
            provider1_id = "padma coffee works",
            payment = OrderPayment(0.0, status = OrderPayment.Status.NOTPAID, transactionId = "abc")
          ), context = context
        )
        retailBengaluruBpp
          .stubFor(
            post("/confirm").willReturn(
              okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
            )
          )

        val confirmOrderResponseString = invokeConfirmOrder(orderRequestForTest)
          .andExpect(MockMvcResultMatchers.status().is4xxClientError)
          .andReturn()
          .response.contentAsString

        verifyConfirmResponseMessage(
          confirmOrderResponseString, orderRequestForTest, ResponseMessage.nack(),
          ProtocolError("BAP_015", "BAP hasn't received payment yet")
        )
        verifyThatBppConfirmApiWasNotInvoked(retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi)
      }

      it("should return null when cart items are empty") {
        val orderRequestForTest = OrderRequestDto(
          message = OrderDtoFactory.create(
            bpp1_id = retailBengaluruBpp.baseUrl(),
            provider1_id = "padma coffee works",
            payment = OrderPayment(100.0, status = OrderPayment.Status.NOTPAID, transactionId = "abc"),
            items = emptyList()
          ), context = context
        )
        retailBengaluruBpp
          .stubFor(
            post("/confirm").willReturn(
              okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
            )
          )

        val confirmOrderResponseString = invokeConfirmOrder(orderRequestForTest)
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        verifyConfirmResponseMessage(confirmOrderResponseString, orderRequestForTest, ResponseMessage.ack())
        verifyThatBppConfirmApiWasNotInvoked(retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi)
      }

      it("should invoke provider confirm api and save message when payment is done") {
        retailBengaluruBpp
          .stubFor(
            post("/confirm").willReturn(
              okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
            )
          )

        val confirmOrderResponseString = invokeConfirmOrder(orderRequest)
          .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
          .andReturn()
          .response.contentAsString

        val confirmOrderResponse =
          verifyConfirmResponseMessage(confirmOrderResponseString, orderRequest, ResponseMessage.ack())
        verifyThatBppConfirmApiWasInvoked(confirmOrderResponse, orderRequest, retailBengaluruBpp)
        verifyThatSubscriberLookupApiWasInvoked(registryBppLookupApi, retailBengaluruBpp)
      }


      it("should return error when empty body request is passed to confirm v2 invoked") {
        retailBengaluruBpp.stubFor(post("/confirm").willReturn(serverError()))
        val getConfirmOrderResponseString = invokeConfirmOrderV2(listOf())
          .andExpect(MockMvcResultMatchers.status().is4xxClientError)
          .andReturn()
          .response.contentAsString

        val getConfirmResponse = objectMapper.readValue(getConfirmOrderResponseString, object : TypeReference<List<ProtocolAckResponse>>(){})
        getConfirmResponse.first().context shouldBe null
        getConfirmResponse.first().message shouldBe  ResponseMessage.nack()
        getConfirmResponse.first().error shouldBe BppError.BadRequestError.error()

      }
      it("should return authentication error when api invoked by invalid token") {
           val authentication: Authentication = Mockito.mock(Authentication::class.java)
           val securityContext: SecurityContext = Mockito.mock(SecurityContext::class.java)
           SecurityContextHolder.setContext(securityContext)
           Mockito.`when`(securityContext.authentication).thenReturn(authentication)
           Mockito.`when`(securityContext.authentication.isAuthenticated).thenReturn(true)
           Mockito.`when`(securityContext.authentication.principal).thenReturn(
             null
           )
           val getConfirmOrderResponseString = invokeConfirmOrderV2(orderRequestList)
             .andExpect(MockMvcResultMatchers.status().isUnauthorized)
             .andReturn()
             .response.contentAsString

           val getConfirmResponse = objectMapper.readValue(getConfirmOrderResponseString, object : TypeReference<List<ProtocolAckResponse>>(){})
           getConfirmResponse.first().context shouldBe null
           getConfirmResponse.first().message shouldBe  ResponseMessage.nack()
           getConfirmResponse.first().error shouldBe BppError.AuthenticationError.autheticationError

         }

      it("should return  confirm order error when network call fails ") {
        setMockAuthentication()
        retailBengaluruBpp.stubFor(post("/confirm").willReturn(serverError()))
        val orderStatusResponseStringWithError =
          invokeConfirmOrderV2(orderRequestList)
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andReturn()
            .response.contentAsString
        val confirmOrderResponse = objectMapper.readValue(orderStatusResponseStringWithError, object : TypeReference<List<ProtocolAckResponse>>() {})
          verifyConfirmResponseMessageV2(confirmOrderResponse,orderRequestList.first(), ResponseMessage.nack(),ProtocolError("BAP_011", "BPP returned error"))
      }
      it("should return confirm order error when network call success but update fails") {
        setMockAuthentication()
        retailBengaluruBpp.stubFor(post("/confirm").willReturn(
          okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
        ))
//        val orderStatusResponseStringWithSuccess =
//          invokeConfirmOrderV2(orderRequestList)
//            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
//            .andReturn()
//            .response.contentAsString
//        val getConfirmResponse = objectMapper.readValue(orderStatusResponseStringWithSuccess, object : TypeReference<List<ProtocolAckResponse>>(){})

        val mockConfirmOrderRepos= mock<ResponseStorageService<OrderResponse, OrderDao>> {
          onGeneric { updateDocByQuery(org.mockito.kotlin.any(), org.mockito.kotlin.any()) }.thenReturn(Either.Left(DatabaseError.OnRead))
        }
        val confirmOrderController = ConfirmOrderController(contextFactory,confirmOrderService,mockConfirmOrderRepos)
        val orderStatusResponseList = confirmOrderController.confirmOrderV2(orderRequestList).body as List<ProtocolAckResponse>
        verifyConfirmResponseMessageV2(orderStatusResponseList,orderRequestList.first(), ResponseMessage.nack(),DatabaseError.OnRead.onReadError)
      }

      it("should return confirm order success when network call & update db success ") {
        setMockAuthentication()
        retailBengaluruBpp.stubFor(post("/confirm").willReturn(
          okJson(objectMapper.writeValueAsString(ResponseFactory.getDefault(contextFactory.create())))
        ))
        val orderStatusResponseStringWithSuccess =
          invokeConfirmOrderV2(orderRequestList)
            .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
            .andReturn()
            .response.contentAsString
        val getConfirmResponse = objectMapper.readValue(orderStatusResponseStringWithSuccess, object : TypeReference<List<ProtocolAckResponse>>(){})
        verifyConfirmResponseMessageV2(getConfirmResponse,orderRequestList.first(), ResponseMessage.ack())
        verifyThatBppConfirmApiWasInvoked(getConfirmResponse.first(), orderRequestList.first(), retailBengaluruBpp)
      }
      registryBppLookupApi.stop()

    }
  }
  private  fun setMockAuthentication(){
    val authentication: Authentication = Mockito.mock(Authentication::class.java)
    val securityContext: SecurityContext = Mockito.mock(SecurityContext::class.java)
    SecurityContextHolder.setContext(securityContext)
    Mockito.`when`(securityContext.authentication).thenReturn(authentication)
    Mockito.`when`(securityContext.authentication.isAuthenticated).thenReturn(true)
    Mockito.`when`(securityContext.authentication.principal).thenReturn(
      User(
        uid = "1234533434343",
        name = "John",
        email = "john@gmail.com",
        isEmailVerified = true
      )
    )
  }
  private fun verifyThatSubscriberLookupApiWasInvoked(
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

  private fun verifyThatBppConfirmApiWasNotInvoked(bppApi: WireMockServer) =
    bppApi.verify(0, postRequestedFor(urlEqualTo("/confirm")))

  private fun verifyThatSubscriberLookupApiWasNotInvoked(registryBppLookupApi: WireMockServer) =
    registryBppLookupApi.verify(0, postRequestedFor(urlEqualTo("/lookup")))

  private fun stubBppLookupApi(
    registryBppLookupApi: WireMockServer,
    providerApi: WireMockServer
  ) {
    registryBppLookupApi
      .stubFor(
        post("/lookup")
          .withRequestBody(matchingJsonPath("$.subscriber_id", equalTo(providerApi.baseUrl())))
          .willReturn(okJson(getSubscriberForBpp(providerApi)))
      )
  }

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

  private fun verifyConfirmResponseMessage(
    confirmOrderResponseString: String,
    orderRequest: OrderRequestDto,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): ProtocolAckResponse {
    val confirmOrderResponse = objectMapper.readValue(confirmOrderResponseString, ProtocolAckResponse::class.java)
    confirmOrderResponse.context shouldNotBe null
    confirmOrderResponse.context?.messageId shouldNotBe null
    confirmOrderResponse.context?.transactionId shouldBe orderRequest.context.transactionId
    confirmOrderResponse.context?.action shouldBe ProtocolContext.Action.CONFIRM
    confirmOrderResponse.message shouldBe expectedMessage
    confirmOrderResponse.error shouldBe expectedError
    return confirmOrderResponse
  }

  private fun verifyThatBppConfirmApiWasInvoked(
    confirmOrderResponse: ProtocolAckResponse,
    orderRequest: OrderRequestDto,
    providerApi: WireMockServer
  ) {
    val protocolConfirmRequest = getProtocolConfirmRequest(confirmOrderResponse, orderRequest)
    providerApi.verify(
      postRequestedFor(urlEqualTo("/confirm"))
        .withRequestBody(equalToJson(objectMapper.writeValueAsString(protocolConfirmRequest)))
    )
  }

  private fun getProtocolConfirmRequest(
    confirmOrderResponse: ProtocolAckResponse,
    orderRequest: OrderRequestDto
  ): ProtocolConfirmRequest {
    val locations =
      orderRequest.message.items?.first()?.provider?.locations?.map { ProtocolSelectMessageSelectedProviderLocations(id = it) }
    val provider =
      orderRequest.message.items?.first()?.provider// todo: does this hold good even for order object or is this gotten from somewhere else?
    return ProtocolConfirmRequest(
      context = confirmOrderResponse.context!!,
      message = ProtocolConfirmRequestMessage(
        order = ProtocolOrder(
          provider = ProtocolSelectMessageSelectedProvider(
            id = provider!!.id,
            locations = locations
          ),
          items = orderRequest.message.items!!.map {
            ProtocolSelectMessageSelectedItems(
              id = it.id,
              quantity = it.quantity
            )
          },
          billing = orderRequest.message.billingInfo,
          fulfillment = ProtocolFulfillment(
            provider_id =  "padma coffee works",
            end = ProtocolFulfillmentEnd(
              contact = ProtocolContact(
                phone = orderRequest.message.deliveryInfo.phone,
                email = orderRequest.message.deliveryInfo.email
              ), location = orderRequest.message.deliveryInfo.location
            ),
            type = "home_delivery",
            customer = ProtocolCustomer(person = ProtocolPerson(name = orderRequest.message.deliveryInfo.name))
          ),
          addOns = emptyList(),
          offers = emptyList(),
          payment = ProtocolPayment(
            params = mapOf("amount" to orderRequest.message.payment!!.paidAmount.toString()),
            status = ProtocolPayment.Status.PAID
          )
        )
      )
    )
  }

  private fun verifyConfirmResponseMessageV2(
    confirmOrderResponse: List<ProtocolAckResponse>,
    orderRequest: OrderRequestDto,
    expectedMessage: ResponseMessage,
    expectedError: ProtocolError? = null
  ): List<ProtocolAckResponse> {
    confirmOrderResponse.first().context shouldNotBe null
    confirmOrderResponse.first().context?.messageId shouldNotBe null
    confirmOrderResponse.first().context?.transactionId shouldBe orderRequest.context.transactionId
    confirmOrderResponse.first().context?.action shouldBe ProtocolContext.Action.CONFIRM
    confirmOrderResponse.first().message shouldBe expectedMessage
    confirmOrderResponse.first().error shouldBe expectedError
    return confirmOrderResponse
  }
  private fun invokeConfirmOrder(orderRequest: OrderRequestDto) = mockMvc.perform(
    MockMvcRequestBuilders.post("/client/v1/confirm_order").header(
      org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
    ).content(objectMapper.writeValueAsString(orderRequest))
  )

  private fun invokeConfirmOrderV2(orderRequest: List<OrderRequestDto>) = mockMvc.perform(
    MockMvcRequestBuilders.post("/client/v2/confirm_order").header(
      org.springframework.http.HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE
    ).content(objectMapper.writeValueAsString(orderRequest))
  )
}
