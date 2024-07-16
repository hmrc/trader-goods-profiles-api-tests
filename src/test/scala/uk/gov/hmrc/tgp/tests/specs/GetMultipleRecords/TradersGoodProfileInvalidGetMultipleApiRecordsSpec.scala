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

package uk.gov.hmrc.tgp.tests.specs.GetMultipleRecords

import org.scalatest.Tag

import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.getResponseJsonFileAsString

import uk.gov.hmrc.tgp.tests.utils.TokenGenerator.generateRandomBearerToken

class TradersGoodProfileInvalidGetMultipleApiRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GetMultipleApiRecord
      extends Tag("uk.gov.hmrc.tgp.tests.specs.InvalidGetTradersGoodProfileGetMultipleApiRecordsSpec")

  private val FolderName       = "GetAPI"
  private val baseUrlForErrors = "/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=0&size=5"
  val ValidEori                = "GB123456789001"
  val InvalidEori              = "GB123456789002"

  private def runScenario(
    identifier: String,
    expectedStatusCode: Int,
    expectedResponseFile: String,
    scenarioDescription: String
  ): Unit =
    Scenario(s"$scenarioDescription for URL $identifier$baseUrlForErrors") {
      val token    = givenGetToken(isValid = true, identifier)
      val response = getMultipleTgpRecord(token, s"$identifier$baseUrlForErrors")

      val statusCode = response.getStatusCode
      println(s"Status code for $scenarioDescription: $statusCode")
      statusCode.shouldBe(expectedStatusCode)

      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

  Feature(
    "Traders Good Profile Confirm EORI and TGP Enrollment API functionality for Invalid GET Multiple API Records call"
  ) {

    val scenarios = List(
      ("GB123456789007", 404, "Scenario_Get_404", "Validate record not found response 404 for GET TGP record API"),
      ("GB123456789008", 405, "Scenario_Get_405", "Validate method not allowed response 405 for GET TGP record API")
    )

    scenarios.foreach { case (identifier, expectedStatusCode, expectedResponseFile, scenarioDescription) =>
      runScenario(identifier, expectedStatusCode, expectedResponseFile, scenarioDescription)
    }

    Scenario("Validate invalid token bearer response 401 for GET TGP record API") {
      val token      = generateRandomBearerToken()
      val response   = getMultipleTgpRecord(token, s"$ValidEori$baseUrlForErrors")
      val statusCode = response.getStatusCode

      println(s"Status code for invalid token scenario: $statusCode")
      statusCode.shouldBe(401)

      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Get_401")

      println(s"Actual response for invalid token scenario: $actualResponse")
      println(s"Expected response for invalid token scenario: $expectedResponse")

      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    Scenario("Validate invalid EORI no response 403 for GET TGP record API") {
      val token      = givenGetToken(isValid = true, ValidEori)
      val response   = getMultipleTgpRecord(token, s"$InvalidEori$baseUrlForErrors")
      val statusCode = response.getStatusCode

      println(s"Status code for invalid EORI scenario: $statusCode")
      statusCode.shouldBe(403)

      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Get_403")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

  }

}
