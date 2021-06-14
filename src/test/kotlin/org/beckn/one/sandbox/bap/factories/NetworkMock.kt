package org.beckn.one.sandbox.bap.factories

import com.github.tomakehurst.wiremock.WireMockServer
import org.beckn.one.sandbox.bap.constants.City
import org.beckn.one.sandbox.bap.constants.Country
import org.beckn.one.sandbox.bap.constants.Domain
import org.beckn.one.sandbox.bap.registry.Subscriber


object NetworkMock {

  val registry = WireMockServer(4000)
  val retailBengaluruBg = WireMockServer(4001)
  val anotherRetailBengaluruBg = WireMockServer(4002)
  val deliveryPuneBg = WireMockServer(4003)
  val retailBengaluruBpp = WireMockServer(4004)
  val anotherRetailBengaluruBpp = WireMockServer(4005)
  val deliveryPuneBpp = WireMockServer(4006)

  fun createBecknNetwork(): List<Subscriber> {
    registry.start()
    retailBengaluruBg.start()
    anotherRetailBengaluruBg.start()
    deliveryPuneBg.start()
    retailBengaluruBpp.start()
    anotherRetailBengaluruBpp.start()
    deliveryPuneBpp.start()

    return listOf(
      getRetailBengaluruBg(),
      getAnotherRetailBengaluruBg(),
      getDeliveryPuneBg(),
      getRetailBengaluruBpp(),
      getAnotherRetailBengaluruBpp(),
      getDeliveryPuneBpp(),
    )
  }

  private fun getRetailBengaluruBg() = createSubscriber(1, retailBengaluruBg)

  private fun getAnotherRetailBengaluruBg() = createSubscriber(2, anotherRetailBengaluruBg)

  private fun getDeliveryPuneBg() = createSubscriber(
    number = 3, mockServer = deliveryPuneBg, city = City.Pune.value, domain = Domain.Delivery.value
  )

  private fun getRetailBengaluruBpp() = createSubscriber(
    number = 4, mockServer = retailBengaluruBpp, type = Subscriber.Type.BPP
  )

  private fun getAnotherRetailBengaluruBpp() = createSubscriber(
    number = 5, mockServer = anotherRetailBengaluruBpp, type = Subscriber.Type.BPP
  )

  private fun getDeliveryPuneBpp() = createSubscriber(
    number = 6,
    mockServer = deliveryPuneBpp,
    city = City.Pune.value,
    domain = Domain.Delivery.value,
    type = Subscriber.Type.BPP
  )

  private fun createSubscriber(
    number: Int,
    mockServer: WireMockServer,
    type: Subscriber.Type = Subscriber.Type.BG,
    domain: String = Domain.Retail.value,
    city: String = City.Bengaluru.value,
    country: String = Country.India.value,
    status: Subscriber.Status = Subscriber.Status.SUBSCRIBED
  ) = Subscriber(
    subscriber_id = "subscriber-$number.network-$number.org",
    subscriber_url = mockServer.baseUrl(),
    type = type,
    domain = domain,
    city = city,
    country = country,
    status = status,
    signing_public_key = "signing_public_key $number",
    encr_public_key = "encr_public_key $number"
  )
}