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

/*
package uk.gov.hmrc.tgp.tests.specs.UpdateRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.{getRequestJsonFileAsString, getResponseJsonFileAsString}

class TradersGoodProfileInvalidUpdatePutRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object UpdatePutApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileInvalidUpdatePutRecordsSpec")

  Feature("Traders Good Profile API functionality for Invalid Update PUT Record API call") {
    val ValidEori = "GB123456789001"
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
      "ValidPayload"                                  -> getRequestJsonFileAsString(FolderName, "Scenario_Update_200"),
      "OptionalPayloadValidation"                     -> getRequestJsonFileAsString(FolderName, "Scenario_Update_400_OptionalFields"),
      "EmptyPayloadValidation"                        -> getRequestJsonFileAsString(FolderName, "Scenario_Update_400_WithEmptyFields"),
      "IncorrectFormatFieldPayload_MandatoryOptional" -> getRequestJsonFileAsString(
        FolderName,
        "Scenario_Update_400_IncorrectFormatField_Mandatory&Optional"
      )
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
        val response   = updatePutTgpRecord(token, identifier, payload)
        val statusCode = response.getStatusCode
        statusCode.shouldBe(expectedStatusCode)
        val actualResponse   = response.getBody.asString()
        val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
        assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
      }

    scenarios.foreach { case (identifier, expectedResponseFile, expectedStatusCode, scenarioDescription) =>
      runScenario(identifier, expectedResponseFile, expectedStatusCode, payloads("ValidPayload"), scenarioDescription)
    }

    runScenario(
      ValidEori,
      "Scenario_Update_400_OptionalFields",
      400,
      payloads("OptionalPayloadValidation"),
      "Validate error message 400 for Update PUT TGP record API Optional values"
    )
    runScenario(
      ValidEori,
      "Scenario_UpdatePut_400_WithEmptyFields",
      400,
      payloads("EmptyPayloadValidation"),
      "Validate error message 400 for Update PUT TGP record API with empty values"
    )
    runScenario(
      ValidEori,
      "Scenario_UpdatePut_400_IncorrectFormatField_Mandatory&Optional",
      400,
      payloads("IncorrectFormatFieldPayload_MandatoryOptional"),
      "Validate error message 400 for Update PUT TGP record API with incorrect values"
    )

  }
}
 */
