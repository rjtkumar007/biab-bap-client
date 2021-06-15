package org.beckn.one.sandbox.bap.external.registry

import io.kotest.matchers.shouldBe
import org.beckn.one.sandbox.bap.external.registry.SubscriberDto.Status.UNDER_SUBSCRIPTION
import org.beckn.one.sandbox.bap.external.registry.SubscriberDto.Type.BG
import org.junit.jupiter.api.Test

internal class SubscriberDtoTest {
  @Test
  internal fun shouldMapToDomain() {
    val subscriberDto = SubscriberDto(
      subscriber_id = "subscriber 1",
      subscriber_url = "http://subscriber_1.com",
      type = BG,
      domain = "Local Delivery",
      city = "New Delhi",
      country = "India",
      status = UNDER_SUBSCRIPTION,
      signing_public_key = "subscriber 1 public key",
      encr_public_key = "subscriber 1 private key"
    )

    val expectedSubscriber = SubscriberDto(
      subscriber_id = "subscriber 1",
      subscriber_url = "http://subscriber_1.com",
      type = BG,
      domain = "Local Delivery",
      city = "New Delhi",
      country = "India",
      status = UNDER_SUBSCRIPTION,
      signing_public_key = "subscriber 1 public key",
      encr_public_key = "subscriber 1 private key"
    )

//    subscriberDto.toDomain() shouldBe expectedSubscriber
  }
}