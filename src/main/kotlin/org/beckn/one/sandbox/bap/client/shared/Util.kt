package org.beckn.one.sandbox.bap.client.shared

import java.util.*

/** This class is to define all common functions or business logic which can be reused in the project */
object Util {
  /** Validate BaseUrl ends with slash or not
   *@param baseUrl String
   * @return baseUrl String
   **/
  fun getBaseUri(baseUrl: String): String {
    return if (baseUrl.endsWith("/", true)) baseUrl else "$baseUrl/"
  }

  fun getRandomString(length: Int = 10) : String {
    val number = Random().nextInt(999999999);
    // this will convert any number sequence into 9 character.
    return String.format("%09d", number);
  }
}