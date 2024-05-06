/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.tgp.tests.common

import scala.util.Random

object FilePath {
  // Below variable is used for fetching Departure XML files
  val defaultFilePathDepartureXML = "src/test/resources/departures/XML/"

  // Below variable is used for fetching Departure JSON files
  val defaultFilePathDepartureJSON = "src/test/resources/departures/JSON/"

  // Below variable is used for fetching Arrival XML files
  val defaultFilePathArrivalsXML = "src/test/resources/arrivals/XML/"

  // Below variable is used for fetching Arrival JSON files
  val defaultFilePathArrivalsJSON = "src/test/resources/arrivals/JSON/"

  // Below variable is used for fetching Departures JSON files for Test Support API
  val defaultFilePathDeparturesJSONTestSupportAPI = "src/test/resources/departures/TestSupportJson/"

  // Below variable is used for fetching Arrival JSON files for Test Support API
  val defaultFilePathArrivalsJSONTestSupportAPI = "src/test/resources/arrivals/TestSupportJson/"

  val defaultFilePathTempXML = "src/test/resources/temp/XML/"

  val defaultFilePathTempJSON = "src/test/resources/temp/JSON/"
}

object InputData {
  def generateEori(): String = {
    val random10DigitNumber = Random.nextInt(1000000000).toString.reverse.padTo(10, '0').reverse
    s"GB$random10DigitNumber"
  }

  def generateLRN(): String =
    Random.alphanumeric.filter(_.isDigit).take(9).mkString

  def generateLrnToUseForEis: String =
    Random.alphanumeric.filter(_.isDigit).take(9).mkString

  def generateMessageSender: String =
    Random.alphanumeric.filter(_.isLetter).take(12).mkString

  val notHexaDeptId: String                   = "wrongdeptid"
  val notHexaArrivalId: String                = "wrongarrivalId"
  val invalidDeptIdWithValidLength: String    = "62aaa7350a481039"
  val invalidArrivalIdWithValidLength: String = "62aaa7350a481039"
  val notHexaMsgId: String                    = "wrongdeptid"
  val invalidMsgId: String                    = "62aaa7350a481039"
  val invalidDateTime: String                 = "2xi2-13-65T17:60:75.513098Z"
  val msgIdNotAssociatedWithDeptId            = "63510478430fdcba"
  val eori                                    = generateEori()
  val invalidLengthMovementEori               = "zasASWFRDY!@£$^&-gt000"
  val validLengthNotInDbMovementEori          = "ACVFR123456789OZH"
  val lrnGenNotInDB                           = "01234567891234567"

}
