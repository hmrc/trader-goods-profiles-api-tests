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

package uk.gov.hmrc.tgp.tests.specs

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.getResponseJsonFileAsString
import uk.gov.hmrc.tgp.tests.utils.TokenGenerator.generateRandomBearerToken

class TradersGoodProfileGetSingleApiRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GetSingleApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.GetTradersGoodProfileSpec")

  Feature("Traders Good Profile Confirm EORI and TGP Enrollment API functionality for GET API call") {
    val scenarios = List(
      ("GB123456789001", 200, "Scenario_Get_Single_200", "Validate success response 200 for GET TGP record API"),
      ("GB123456789002", 404, "Scenario_Get_404", "Validate record not found response 404 for GET TGP record API"),
      (
        "GB123456789003",
        400,
        "Scenario_Get_400",
        "Validate invalid recordID format response 400 for GET TGP record API"
      ),
      ("GB123456789004", 404, "Scenario_Get_404", "Validate invalid URL response 404 for GET TGP record API"),
      ("GB123456789005", 405, "Scenario_Get_405", "Validate method not allowed response 405 for GET TGP record API"),
      ("GB123456789006", 500, "Scenario_Get_500", "Validate internal server error response 500 for GET TGP record API")
    )

    scenarios.foreach { case (identifier, expectedStatusCode, expectedResponseFile, scenarioDescription) =>
      Scenario(s"GET TGP SINGLE RECORD - $scenarioDescription") {
        val token      = givenGetToken(isValid = true, identifier)
        println(token)
        val response   = getTgpRecord(token, identifier)
        val statusCode = response.getStatusCode
        System.out.println("Status code: " + statusCode)
        statusCode.shouldBe(expectedStatusCode)
        val actualResponse   = response.getBody.asString()
        val expectedResponse = getResponseJsonFileAsString(expectedResponseFile)
        assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")

      }
    }

    Scenario(s"GET TGP SINGLE RECORD -Validate invalid token bearer response 401 for GET TGP record API") {
      val token      = generateRandomBearerToken()
      val response   = getTgpRecord(token, "GB123456789001")
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(401)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString("Scenario_Get_401")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")

    }

    Scenario(s"GET TGP SINGLE RECORD -Validate invalid EORI no response 403 for GET TGP record API") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = getTgpRecord(token, "GB123456789002")
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(403)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString("Scenario_Get_403")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")

    }
  }
}
