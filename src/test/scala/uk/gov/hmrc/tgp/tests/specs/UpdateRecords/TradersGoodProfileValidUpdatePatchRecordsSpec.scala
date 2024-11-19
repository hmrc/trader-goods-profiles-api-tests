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

package uk.gov.hmrc.tgp.tests.specs.UpdateRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.{getRequestJsonFileAsString, getResponseJsonFileAsString}

class TradersGoodProfileValidUpdatePatchRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object UpdatePatchApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileValidUpdatePatchRecordsSpec")

  Feature("Traders Good Profile API functionality for Valid Update PATCH Record API call") {

    val FolderName = "UpdateAPI"
    val ValidEori  = "GB123456789001"

    val stubbedOnlyMandatoryResponseEori = "GB987789562345"

    val payloads = Map(
      "ValidPayload"              -> getRequestJsonFileAsString(FolderName, "Scenario_Update_200"),
      "MandatoryPayload"          -> getRequestJsonFileAsString(FolderName, "Scenario_UpdatePatch_200_OnlyMandatory"),
      "PayloadWithMaxFieldValues" -> getRequestJsonFileAsString(FolderName, "Scenario_Update_200_WithAllMaxLength"),
      "PayloadWithMinFieldValues" -> getRequestJsonFileAsString(FolderName, "Scenario_Update_200_WithAllMinLength")
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
        val response   = updatePatchTgpRecord(token, identifier, payload)
        val statusCode = response.getStatusCode
        statusCode.shouldBe(expectedStatusCode)
        val actualResponse   = response.getBody.asString()
        val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
        assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
      }

    runScenario(
      ValidEori,
      "Scenario_UpdatePatch_200",
      200,
      payloads("ValidPayload"),
      "Validate success 200 for Update PATCH TGP record API"
    )
    runScenario(
      stubbedOnlyMandatoryResponseEori,
      "Scenario_UpdatePatch_200_OnlyMandatory",
      200,
      payloads("MandatoryPayload"),
      "Validate success 200 for Update PATCH TGP record API with only Mandatory values"
    )
    runScenario(
      ValidEori,
      "Scenario_UpdatePatch_200_WithAllMaxLength",
      200,
      payloads("PayloadWithMaxFieldValues"),
      "Validate success 200 for Update PATCH TGP record API with all max values"
    )
    runScenario(
      ValidEori,
      "Scenario_UpdatePatch_200_WithAllMinLength",
      200,
      payloads("PayloadWithMinFieldValues"),
      "Validate success 200 for Update PATCH TGP record API with all min values"
    )

  }
}
