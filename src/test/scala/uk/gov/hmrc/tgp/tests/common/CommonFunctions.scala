

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
