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

package uk.gov.hmrc.tgp.tests.specs.CreateRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.{getRequestJsonFileAsString, getResponseJsonFileAsString}

class TradersGoodProfileValidCreateRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object CreateApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileValidCreateRecordsSpec")

  private val FolderName = "CreateAPI"

  private def getPayload(scenario: String): String = getRequestJsonFileAsString(FolderName, scenario)

  private def validateSuccess201(
    identifier: String,
    payloadFile: String,
    expectedResponseFile: String,
    scenarioDescription: String
  ): Unit =
    Scenario(s"CREATE TGP RECORD - $scenarioDescription") {
      val token      = givenGetToken(isValid = true, identifier)
      val response   = createTgpRecord(token, identifier, getPayload(payloadFile))
      val statusCode = response.getStatusCode
      statusCode.shouldBe(201)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

  // Define reusable payloads
  private val ValidEori                 = "GB123456789001"
  private val ValidPayload              = "Scenario_Create_201"
  private val MandatoryPayload          = "Scenario_Create_201_OnlyMandatory"
  private val PayloadWithMaxFieldValues = "Scenario_Create_201_WithAllMaxLength"
  private val PayloadWithMinFieldValues = "Scenario_Create_201_WithAllMinLength"

  // Execute success 201 scenarios
  validateSuccess201(ValidEori, ValidPayload, "Scenario_Create_201", "Validate success 201 for Create TGP record API")
  validateSuccess201(
    ValidEori,
    MandatoryPayload,
    "Scenario_Create_201_OnlyMandatory",
    "Validate success 201 for Create TGP record API with only Mandatory values"
  )
  validateSuccess201(
    ValidEori,
    PayloadWithMaxFieldValues,
    "Scenario_Create_201_WithAllMaxLength",
    "Validate success 201 for Create TGP record API with all max values"
  )
  validateSuccess201(
    ValidEori,
    PayloadWithMinFieldValues,
    "Scenario_Create_201_WithAllMinLength",
    "Validate success 201 for Create TGP record API with all min values"
  )

}
