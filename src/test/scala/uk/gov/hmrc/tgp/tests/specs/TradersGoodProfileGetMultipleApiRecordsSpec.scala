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

class TradersGoodProfileGetMultipleApiRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GetMultipleApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.GetTradersGoodProfileGetMultipleApiRecordsSpec")

  Feature("Traders Good Profile Confirm EORI and TGP Enrollment API functionality for GET Multiple API Records call") {
    val scenarios = List(
      ("GB123456789001", 200, "Scenario_Get_Multiple_200", "Validate success response 200 for GET TGP record API"),
      (
        "GB123456789003",
        200,
        "Scenario_Get_Multiple_ZeroRecord_200",
        "Validate Zero record found response 200 for GET TGP record API"
      ),
      ("GB123456789007", 404, "Scenario_Get_404", "Validate record not found response 404 for GET TGP record API"),
      ("GB123456789008", 405, "Scenario_Get_405", "Validate method not allowed response 405 for GET TGP record API"),
      ("GB123456789006", 500, "Scenario_Get_500", "Validate internal server error response 500 for GET TGP record API")
    )

    val urlsFor200       = List(
      "GB123456789001/records?page=1&size=5",
      "GB123456789001/records?page=1",
      "GB123456789001/records?size=5",
      "GB123456789001/records?lastUpdatedDate=2024-03-26T16:14:52Z",
      "GB123456789001/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=1&size=13",
      "GB123456789001/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=1",
      "GB123456789001/records?lastUpdatedDate=2024-03-26T16:14:52Z&size=5"
    )
    val FolderName       = "GetAPI"
    val baseUrlForErrors = "/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=1&size=5"

    scenarios.foreach { case (identifier, expectedStatusCode, expectedResponseFile, scenarioDescription) =>
      if (identifier == "GB123456789001" && expectedStatusCode == 200) {
        urlsFor200.foreach { url =>
          Scenario(s"GET TGP MULTIPLE RECORDS - $scenarioDescription for URL $url") {
            val token      = givenGetToken(isValid = true, identifier)
            println(token)
            val response   = getMultipleTgpRecord(token, url)
            val statusCode = response.getStatusCode
            System.out.println("Status code: " + statusCode)
            statusCode.shouldBe(expectedStatusCode)
          }
        }
      } else {
        val errorUrl = s"$identifier$baseUrlForErrors"
        Scenario(s"GET TGP MULTIPLE RECORDS - $scenarioDescription for URL $errorUrl") {
          val token      = givenGetToken(isValid = true, identifier)
          println(token)
          val response   = getMultipleTgpRecord(token, errorUrl)
          val statusCode = response.getStatusCode
          System.out.println("Status code: " + statusCode)
          statusCode.shouldBe(expectedStatusCode)
          // Validate JSON response for error scenarios
          val actualResponse   = response.getBody.asString()
          // val expectedResponse = getResponseJsonFileAsString(expectedResponseFile)
          val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
          assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
        }
      }
    }

    Scenario(s"GET TGP MULTIPLE RECORDS - Validate invalid token bearer response 401 for GET TGP record API") {
      val token      = generateRandomBearerToken()
      val response   = getMultipleTgpRecord(token, s"GB123456789001$baseUrlForErrors")
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(401)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Get_401")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    Scenario(s"GET TGP MULTIPLE RECORDS - Validate invalid EORI no response 403 for GET TGP record API") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = getMultipleTgpRecord(token, s"GB123456789002$baseUrlForErrors")
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(403)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Get_403")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }
  }

}
