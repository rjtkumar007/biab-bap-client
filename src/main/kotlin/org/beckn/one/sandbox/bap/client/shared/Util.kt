package org.beckn.one.sandbox.bap.client.shared

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

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


  fun isValidPincode(pinCode: String): Boolean {
    val regex = "^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$"
    val p: Pattern = Pattern.compile(regex)
    if (pinCode == null) {
      return false
    }
    val m: Matcher = p.matcher(pinCode)
    return m.matches()
  }
}