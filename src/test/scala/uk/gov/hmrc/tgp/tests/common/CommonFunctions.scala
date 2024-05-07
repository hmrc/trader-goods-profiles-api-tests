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

  val eori = generateEori()

}
