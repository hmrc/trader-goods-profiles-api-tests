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

package uk.gov.hmrc.tgp.tests.specs.GetSingleRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.getResponseJsonFileAsString
import uk.gov.hmrc.tgp.tests.utils.TokenGenerator.generateRandomBearerToken

class TradersGoodProfileInvalidGetSingleApiRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GetSingleApiRecord
      extends Tag("uk.gov.hmrc.tgp.tests.specs.InvalidGetTradersGoodProfileGetSingleApiRecordsSpec")

  private val FolderName  = "GetAPI"
  private val ValidEori   = "GB123456789001"
  private val InvalidEori = "GB123456789002"

  private def getPayload(scenario: String): String = getResponseJsonFileAsString(FolderName, scenario)

  private def validateStatusCodeAndResponse(
    identifier: String,
    expectedStatusCode: Int,
    expectedResponseFile: String,
    scenarioDescription: String
  ): Unit =
    Scenario(s"GET TGP SINGLE RECORD - $scenarioDescription") {
      val token      = givenGetToken(isValid = true, identifier)
      val response   = getTgpRecord(token, identifier)
      val statusCode = response.getStatusCode
      statusCode.shouldBe(expectedStatusCode)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getPayload(expectedResponseFile)
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

  // Define scenarios
  private val scenarios = List(
    ("GB123456789002", 404, "Scenario_Get_404", "Validate record not found response 404 for GET TGP record API"),
    ("GB123456789003", 400, "Scenario_Get_400", "Validate invalid recordID format response 400 for GET TGP record API"),
    ("GB123456789004", 404, "Scenario_Get_404", "Validate invalid URL response 404 for GET TGP record API"),
    ("GB123456789005", 405, "Scenario_Get_405", "Validate method not allowed response 405 for GET TGP record API")
  )

  // Execute each scenario
  scenarios.foreach { case (identifier, expectedStatusCode, expectedResponseFile, scenarioDescription) =>
    validateStatusCodeAndResponse(identifier, expectedStatusCode, expectedResponseFile, scenarioDescription)
  }

  // Additional scenarios
  Scenario(s"GET TGP SINGLE RECORD -Validate invalid token bearer response 401 for GET TGP record API") {
    val token      = generateRandomBearerToken()
    val response   = getTgpRecord(token, ValidEori)
    val statusCode = response.getStatusCode
    statusCode.shouldBe(401)
    val actualResponse   = response.getBody.asString()
    val expectedResponse = getPayload("Scenario_Get_401")
    assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
  }

  Scenario(s"GET TGP SINGLE RECORD -Validate invalid EORI no response 403 for GET TGP record API") {
    val token      = givenGetToken(isValid = true, ValidEori)
    val response   = getTgpRecord(token, InvalidEori)
    val statusCode = response.getStatusCode
    statusCode.shouldBe(403)
    val actualResponse   = response.getBody.asString()
    val expectedResponse = getPayload("Scenario_Get_403")
    assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
  }

}
