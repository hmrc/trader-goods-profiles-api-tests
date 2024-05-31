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
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.getRequestJsonFileAsString
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.getResponseJsonFileAsString

class TradersGoodProfileCreateRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GetApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.CreateTradersGoodProfileSpec")

  Feature("Traders Good Profile API functionality for Create Record API call") {

    val scenarios = List(
      ("GB123456789007", 500, "Unauthorized", "Validate internal server error response 500 for Create record API"),
      ("GB123456789005", 404, "Not Found", "Validate method not found 404 for Create record API"),
      ("GB123456789006", 405, "Method Not Allowed", "Validate method not allowed response 405 for Create record API")
    )

    val FolderName = "CreateAPI"

    var ValidPayload = "Scenario_Create_201"
    ValidPayload = getRequestJsonFileAsString(FolderName, ValidPayload)

    var MandatoryPayload = "Scenario_Create_201_OnlyMandatory"
    MandatoryPayload = getRequestJsonFileAsString(FolderName, MandatoryPayload)

    var PayloadWithMaxFieldValues = "Scenario_Create_201_WithAllMaxLength"
    PayloadWithMaxFieldValues = getRequestJsonFileAsString(FolderName, PayloadWithMaxFieldValues)

    var MandatoryPayloadValidation = "Scenario_Create_400_MandatoryFields"
    MandatoryPayloadValidation = getRequestJsonFileAsString(FolderName, MandatoryPayloadValidation)

    var OptionalPayloadValidation = "Scenario_Create_400_OptionalFields"
    OptionalPayloadValidation = getRequestJsonFileAsString(FolderName, OptionalPayloadValidation)

    var EmptyPayloadValidation = "Scenario_Create_400_WithEmptyFields"
    EmptyPayloadValidation = getRequestJsonFileAsString(FolderName, EmptyPayloadValidation)

    scenarios.foreach { case (identifier, expectedStatusCode, expectedErrorMessage, scenarioDescription) =>
      Scenario(s"CREATE TGP SINGLE RECORD - $scenarioDescription") {
        val token      = givenGetToken(isValid = true, identifier)
        val response   = createTgpRecord(token, identifier, ValidPayload)
        val statusCode = response.getStatusCode
        statusCode.shouldBe(expectedStatusCode)
        val actualResponse = response.getBody.asString()
        assert(actualResponse.contains(expectedErrorMessage))
      }
    }

    Scenario(s"CREATE TGP RECORD - Validate success 201 for Create TGP record API") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = createTgpRecord(token, "GB123456789001", ValidPayload)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(201)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Create_201")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    Scenario(s"CREATE TGP RECORD - Validate success 201 for Create TGP record API with only Mandatory values") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = createTgpRecord(token, "GB123456789001", MandatoryPayload)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(201)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Create_201_OnlyMandatory")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    Scenario(s"CREATE TGP RECORD - Validate success 201 for Create TGP record API with all max values") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = createTgpRecord(token, "GB123456789001", PayloadWithMaxFieldValues)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(201)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Create_201_WithAllMaxLength")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    Scenario(s"CREATE TGP RECORD - Validate error message 400 for Create TGP record API Mandatory values") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = createTgpRecord(token, "GB123456789001", MandatoryPayloadValidation)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(400)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Create_400_MandatoryFields")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    Scenario(s"CREATE TGP RECORD - Validate error message 400 for Create TGP record API Optional values") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = createTgpRecord(token, "GB123456789001", OptionalPayloadValidation)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(400)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Create_400_OptionalFields")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    Scenario(s"CREATE TGP RECORD - Validate error message 400 for Create TGP record API with empty values") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = createTgpRecord(token, "GB123456789001", EmptyPayloadValidation)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(400)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Create_400_WithEmptyFields")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

  }
}
