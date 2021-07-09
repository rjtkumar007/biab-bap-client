package org.beckn.one.sandbox.bap.client.controllers

import org.beckn.one.sandbox.bap.client.dtos.CartDto
import org.beckn.one.sandbox.bap.client.dtos.CartItemDto
import org.beckn.one.sandbox.bap.client.dtos.CartItemProviderDto
import org.beckn.one.sandbox.bap.client.dtos.CartSelectedItemQuantity
import org.beckn.one.sandbox.bap.configurations.JacksonConfiguration
import org.beckn.one.sandbox.bap.schemas.*
import java.time.OffsetDateTime
import java.util.*

fun main() {
  val c = CartDto(
    items = listOf(
      CartItemDto(
        id = "./retail.kirana/ind.blr/800@mandi.succinct.in.item",
        descriptor = ProtocolDescriptor(
          name = "Tropicana Orange Juice-200 ml",
        ),
        bppId = "./retail.kirana/ind.blr/mandi.succinct.in",
        bppUri = "https://mandi.succinct.in/bpp",
        provider = CartItemProviderDto(
          id = "./retail.kirana/ind.blr/84@mandi.succinct.in.provider",
          locations = listOf("./retail.kirana/ind.blr/36@mandi.succinct.in.provider_location")
        ),
        quantity = CartSelectedItemQuantity(
          count = 1
        )
      )
    ),
    transactionId = UUID.randomUUID().toString()
  )
  val x = ProtocolSelectRequest(
    context = ProtocolContext(
      domain = "LOCAL-RETAIL",
      country = "IND",
      city = "Bengaluru",
      action = ProtocolContext.Action.SELECT,
      coreVersion = "0.9.1",
      bapId = "qa.api.box.beckn.org",
      bapUri = "http://qa.api.box.beckn.org/bap/v1",
      bppId = null,
      bppUri = null,
      transactionId = "75bb2770-72b5-44bb-b57e-8b8e330eb272",
      messageId = "1db1b8ce-170d-4598-9594-9d477914886c",
      timestamp = OffsetDateTime.now(),
      key = null,
      ttl = null
    ),
    message = ProtocolSelectRequestMessage(
      selected = ProtocolOnSelectMessageSelected(
        provider = ProtocolProvider(
          id = "./retail.kirana/ind.blr/84@mandi.succinct.in.provider",
          locations = listOf(
            ProtocolLocation(
              descriptor = null, id = "./retail.kirana/ind.blr/36@mandi.succinct.in.provider_location"
            )
          )
        ),
        items = listOf(
          ProtocolSelectedItem(
            id = "./retail.kirana/ind.blr/800@mandi.succinct.in.item",
            descriptor = ProtocolDescriptor(name = "Tropicana Orange Juice - 200 ml"),
            quantity = ProtocolSelectedItemQuantity(count = 1, measure = null)
          )
        )
      )
    )
  )
  println(JacksonConfiguration().objectMapper().writeValueAsString(x))
}