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

class TradersGoodProfileUpdateRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object UpdateApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileUpdateRecordsSpec")

  Feature("Traders Good Profile API functionality for Update Record API call") {

    val scenarios = List(
      (
        "GB123456789002",
        "Scenario_Update_Duplicate_Trader_400",
        400,
        "Validate trying to update a record with a duplicate traderRef error response 400 for Update record API"
      ),
      (
        "GB123456789003",
        "Scenario_Update_RecordIdNotExist_400",
        400,
        "Validate there is an ongoing accreditation request and the record cannot be updated error response 400 for Update record API"
      ),
      (
        "GB123456789004",
        "Scenario_Update_RecordIdRemovedLocked_400",
        400,
        "Validate this record has been removed and cannot be updated error response 400 for Update record API"
      )
    )

    val FolderName = "UpdateAPI"

    val payloads = Map(
      "ValidPayload"              -> getRequestJsonFileAsString(FolderName, "Scenario_Update_200"),
      "MandatoryPayload"          -> getRequestJsonFileAsString(FolderName, "Scenario_Update_200_OnlyMandatory"),
      "PayloadWithMaxFieldValues" -> getRequestJsonFileAsString(FolderName, "Scenario_Update_200_WithAllMaxLength"),
      "OptionalPayloadValidation" -> getRequestJsonFileAsString(FolderName, "Scenario_Update_400_OptionalFields"),
      "EmptyPayloadValidation"    -> getRequestJsonFileAsString(FolderName, "Scenario_Update_400_WithEmptyFields")
    )

    def runScenario(
      identifier: String,
      expectedResponseFile: String,
      expectedStatusCode: Int,
      payload: String,
      description: String
    ): Unit =
      Scenario(s"UPDATE TGP RECORD - $description") {
        val token      = givenGetToken(isValid = true, identifier)
        val response   = updateTgpRecord(token, identifier, payload)
        val statusCode = response.getStatusCode
        System.out.println("Status code: " + statusCode)
        statusCode.shouldBe(expectedStatusCode)
        val actualResponse   = response.getBody.asString()
        val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
        assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
      }

    scenarios.foreach { case (identifier, expectedResponseFile, expectedStatusCode, scenarioDescription) =>
      runScenario(identifier, expectedResponseFile, expectedStatusCode, payloads("ValidPayload"), scenarioDescription)
    }

    runScenario(
      "GB123456789001",
      "Scenario_Update_200",
      200,
      payloads("ValidPayload"),
      "Validate success 200 for Update TGP record API"
    )
    runScenario(
      "GB123456789001",
      "Scenario_Update_200_OnlyMandatory",
      200,
      payloads("MandatoryPayload"),
      "Validate success 200 for Update TGP record API with only Mandatory values"
    )
    runScenario(
      "GB123456789001",
      "Scenario_Update_200_WithAllMaxLength",
      200,
      payloads("PayloadWithMaxFieldValues"),
      "Validate success 200 for Update TGP record API with all max values"
    )
    runScenario(
      "GB123456789001",
      "Scenario_Update_400_OptionalFields",
      400,
      payloads("OptionalPayloadValidation"),
      "Validate error message 400 for Update TGP record API Optional values"
    )
    runScenario(
      "GB123456789001",
      "Scenario_Update_400_WithEmptyFields",
      400,
      payloads("EmptyPayloadValidation"),
      "Validate error message 400 for Create TGP record API with empty values"
    )

  }
}
