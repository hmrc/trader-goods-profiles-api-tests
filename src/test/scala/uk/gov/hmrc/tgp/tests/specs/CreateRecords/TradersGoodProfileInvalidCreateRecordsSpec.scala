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

class TradersGoodProfileInvalidCreateRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object CreateApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileCreateRecordsSpec")

  private val FolderName = "CreateAPI"

  private def getPayload(scenario: String): String = getRequestJsonFileAsString(FolderName, scenario)

  private def validateErrorResponse(
    identifier: String,
    expectedStatusCode: Int,
    payloadFile: String,
    expectedErrorMessage: String,
    scenarioDescription: String
  ): Unit =
    Scenario(s"CREATE TGP SINGLE RECORD - $scenarioDescription") {
      val token      = givenGetToken(isValid = true, identifier)
      val response   = createTgpRecord(token, identifier, getPayload(payloadFile))
      val statusCode = response.getStatusCode
      statusCode.shouldBe(expectedStatusCode)
      val actualResponse = response.getBody.asString()
      assert(actualResponse.contains(expectedErrorMessage))
    }

  private def validateError400Response(
    identifier: String,
    payloadFile: String,
    expectedResponseFile: String,
    scenarioDescription: String
  ): Unit =
    Scenario(s"CREATE TGP RECORD - $scenarioDescription") {
      val token      = givenGetToken(isValid = true, identifier)
      val response   = createTgpRecord(token, identifier, getPayload(payloadFile))
      val statusCode = response.getStatusCode
      statusCode.shouldBe(400)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

  // Define reusable payloads
  private val ValidEori                                     = "GB123456789001"
  private val ValidPayload                                  = "Scenario_Create_201"
  private val MandatoryPayloadValidation                    = "Scenario_Create_400_MandatoryFields"
  private val OptionalPayloadValidation                     = "Scenario_Create_400_OptionalFields"
  private val EmptyPayloadValidation                        = "Scenario_Create_400_WithEmptyFields"
  private val IncorrectFormatFieldPayload_MandatoryOptional =
    "Scenario_Create_400_IncorrectFormatField_Mandatory&Optional"

  // Define scenarios
  private val errorScenarios = List(
    ("GB123456789005", 404, "Not Found", "Validate method not found 404 for Create record API"),
    ("GB123456789006", 405, "Method Not Allowed", "Validate method not allowed response 405 for Create record API")
  )

  // Execute error scenarios
  errorScenarios.foreach { case (identifier, expectedStatusCode, expectedErrorMessage, scenarioDescription) =>
    validateErrorResponse(
      identifier,
      expectedStatusCode,
      ValidPayload,
      expectedErrorMessage,
      scenarioDescription
    )
  }

  // Execute 400 error validation scenarios
  validateError400Response(
    ValidEori,
    MandatoryPayloadValidation,
    "Scenario_Create_400_MandatoryFields",
    "Validate error message 400 for Create TGP record API Mandatory values"
  )
  validateError400Response(
    ValidEori,
    OptionalPayloadValidation,
    "Scenario_Create_400_OptionalFields",
    "Validate error message 400 for Create TGP record API Optional values"
  )
  validateError400Response(
    ValidEori,
    EmptyPayloadValidation,
    "Scenario_Create_400_WithEmptyFields",
    "Validate error message 400 for Create TGP record API with empty values"
  )
  validateError400Response(
    ValidEori,
    IncorrectFormatFieldPayload_MandatoryOptional,
    "Scenario_Create_400_IncorrectFormatField_Mandatory&Optional",
    "Validate error message 400 for Create TGP record API with Incorrect Values"
  )

}
