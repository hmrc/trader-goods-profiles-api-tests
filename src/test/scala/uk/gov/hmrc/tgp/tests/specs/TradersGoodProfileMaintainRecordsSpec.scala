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
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.{getRequestJsonFileAsString, getResponseJsonFileAsString}

class TradersGoodProfileMaintainRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object MaintainApiRecords extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileMaintainRecordsSpec")

  Feature("Traders Good Profile API functionality to Maintain Records API call") {

    val scenarios = List(
      ("GB123456789001", 200, "Scenario_Maintain_200", "Validate success response 200 for Maintain Records API call"),
      (
        "GB123456789004",
        404,
        "Scenario_Maintain_NotFound_404",
        "Validate the error 'Not Found' 404 for Maintain Records API call"
      ),
      (
        "GB123456789007",
        500,
        "Scenario_Maintain_Unauthorized_500",
        "Validate the error 'Unauthorized' 500 for Maintain Records API call"
      ),
      (
        "GB123456789006",
        500,
        "Scenario_Maintain_InternalServer_500",
        "Validate the error 'Internal Server Error' 500 for Maintain Records API call"
      ),
      (
        "GB123456789005",
        405,
        "Scenario_Maintain_MethodNotAllowed_405",
        "Validate the error 'Method Not Allowed' 405 for Maintain Records API call"
      ),
      (
        "GB123456789008",
        500,
        "Scenario_Maintain_EmptyPayload_500",
        "Validate the error 'Internal server error with 200 empty payload' 500 for Maintain Records API call"
      )
    )

    val FolderName = "MaintainRecordAPI"

    def getPayload(scenario: String): String = getRequestJsonFileAsString(FolderName, scenario)

    scenarios.foreach { case (identifier, expectedStatusCode, expectedResponseFile, scenarioDescription) =>
      Scenario(s"Maintain Records Api - $scenarioDescription") {
        val token      = givenGetToken(isValid = true, identifier)
        val response   = maintainRecord(token, identifier, getPayload("Scenario_Maintain_200"))
        val statusCode = response.getStatusCode
        System.out.println("Status code: " + statusCode)
        statusCode.shouldBe(expectedStatusCode)
        val actualResponse   = response.getBody.asString()
        System.out.println("Response: " + actualResponse)
        val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
        assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
      }
    }

    Scenario(
      s"MAINTAIN TGP RECORD - Verify 400 response for Maintain Records API when mandatory field is omitted"
    ) {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   =
        maintainRecord(token, "GB123456789001", getPayload("Scenario_Maintain_RemovingMandatoryFields_400"))
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(400)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Maintain_RemovingMandatoryFields_400")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    Scenario(s"MAINTAIN TGP RECORD - Validate success 200 for Maintain TGP record API with only Mandatory values") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = maintainRecord(token, "GB123456789001", getPayload("Scenario_Maintain_OnlyMandatoryFields_200"))
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(200)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Maintain_OnlyMandatoryFields_200")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    Scenario(s"MAINTAIN TGP RECORD - Validate error for Maintain TGP record API when invalid limit is provided") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = maintainRecord(token, "GB123456789001", getPayload("Scenario_Maintain_InvalidLimit_400"))
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(400)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Maintain_InvalidLimit_400")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    Scenario(s"MAINTAIN TGP RECORD - Validate Forbidden response 403 for Maintain Records API") {
      val token      = givenGetToken(isValid = true, "GB123456789002")
      val response   = maintainRecord(token, "GB123456789001", getPayload("Scenario_Maintain_200"))
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(403)
      val actualResponse = response.getBody.asString()
      assert(actualResponse.contains("EORI number is incorrect"))
    }

  }
}
