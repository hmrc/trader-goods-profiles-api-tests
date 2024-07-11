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

package uk.gov.hmrc.tgp.tests.specs.MaintainRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.{getRequestJsonFileAsString, getResponseJsonFileAsString}

class TradersGoodProfileInvalidMaintainRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object MaintainApiRecords extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileInvalidMaintainRecordsSpec")

  private val FolderName  = "MaintainRecordAPI"
  private val ValidEori   = "GB123456789001"
  private val InvalidEori = "GB123456789002"

  private def performMaintainRecordTest(
    identifier: String,
    expectedStatusCode: Int,
    payloadFile: String,
    expectedResponseFile: String,
    scenarioDescription: String
  ): Unit =
    Scenario(s"Maintain Records Api - $scenarioDescription") {
      val token      = givenGetToken(isValid = true, identifier)
      val response   = maintainRecord(token, identifier, getRequestJsonFileAsString(FolderName, payloadFile))
      val statusCode = response.getStatusCode
      println(s"Status code: $statusCode")
      statusCode.shouldBe(expectedStatusCode)
      val actualResponse   = response.getBody.asString()
      println(s"Response: $actualResponse")
      val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

  private val scenarios = List(
    (
      "GB123456789004",
      404,
      "Scenario_Maintain_NotFound_404",
      "Scenario_Maintain_200",
      "Validate the error 'Not Found' 404 for Maintain Records API call"
    ),
    (
      "GB123456789005",
      405,
      "Scenario_Maintain_MethodNotAllowed_405",
      "Scenario_Maintain_200",
      "Validate the error 'Method Not Allowed' 405 for Maintain Records API call"
    )
  )

  scenarios.foreach { case (identifier, expectedStatusCode, expectedResponseFile, payloadFile, scenarioDescription) =>
    performMaintainRecordTest(identifier, expectedStatusCode, payloadFile, expectedResponseFile, scenarioDescription)
  }

  private def validateBadRequestScenario(token: String, eori: String, payloadFile: String): Unit = {
    val response   = maintainRecord(token, eori, getRequestJsonFileAsString(FolderName, payloadFile))
    val statusCode = response.getStatusCode
    println(s"Status code: $statusCode")
    statusCode.shouldBe(400)
    val actualResponse   = response.getBody.asString()
    val expectedResponse = getResponseJsonFileAsString(FolderName, payloadFile)
    assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
  }

  Scenario(
    s"MAINTAIN TGP RECORD - Verify 400 response for Maintain Records API when mandatory field is omitted"
  ) {
    val token = givenGetToken(isValid = true, ValidEori)
    validateBadRequestScenario(token, ValidEori, "Scenario_Maintain_RemovingMandatoryFields_400")
  }

  Scenario(
    s"MAINTAIN TGP RECORD - Verify 400 response for Maintain Records API with multiple error messages by Missing mandatory and incorrect format fields"
  ) {
    val token = givenGetToken(isValid = true, ValidEori)
    validateBadRequestScenario(token, ValidEori, "Scenario_Maintain_MissingMandatoryAndIncorrectFormatFields_400")
  }

  Scenario(s"MAINTAIN TGP RECORD - Validate error for Maintain TGP record API when invalid limit is provided") {
    val token = givenGetToken(isValid = true, ValidEori)
    validateBadRequestScenario(token, ValidEori, "Scenario_Maintain_InvalidLimit_400")
  }

  Scenario(s"MAINTAIN TGP RECORD - Validate Forbidden response 403 for Maintain Records API") {
    val token      = givenGetToken(isValid = true, InvalidEori)
    val response   = maintainRecord(token, ValidEori, getRequestJsonFileAsString(FolderName, "Scenario_Maintain_200"))
    val statusCode = response.getStatusCode
    println(s"Status code: $statusCode")
    statusCode.shouldBe(403)
    val actualResponse = response.getBody.asString()
    assert(actualResponse.contains("EORI number is incorrect"))
  }

}
