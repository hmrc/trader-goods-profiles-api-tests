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

class TradersGoodProfileRemoveRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GetApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.GetTradersGoodProfileSpec")

  Feature("Traders Good Profile API functionality for Remove Record API call") {

    val scenarios                = List(
      (
        "GB123456789002",
        400,
        "Mandatory field eori was missing from body or is in the wrong format",
        "Validate Invalid request parameter (Mandatory field eori) response 400 for Remove TGP record API"
      ),
      (
        "GB123456789003",
        400,
        "The recordId has been provided in the wrong format",
        "Validate Invalid request parameter (recordId) response 400 for Remove TGP record API"
      ),
      (
        "GB123456789004",
        400,
        "Mandatory field actorId was missing from body or is in the wrong format",
        "Validate Invalid request parameter (actorId) response 400 for Remove TGP record API"
      ),
      ("GB123456789005", 500, "Unauthorized", "Validate internal server error response 500 for Remove record API"),
      ("GB123456789006", 404, "Not Found", "Validate method not found 404 for Remove record API"),
      ("GB123456789007", 405, "Method Not Allowed", "Validate method not allowed response 405 for Remove record API")
    )
    val ValidPayload = "RemoveWithValidActorId"
    val validRequestBody              = getRequestJsonFileAsString(ValidPayload)

    val EmptyPayload = "RemoveWithNoActorId"
    val emptyRequestBody            = getRequestJsonFileAsString(EmptyPayload)

    val InvalidPayload = "RemoveWithInValidActorId"
    val invalidRequestBody              = getRequestJsonFileAsString(InvalidPayload)

    scenarios.foreach { case (identifier, expectedStatusCode, expectedErrorMessage, scenarioDescription) =>
      Scenario(s"REMOVE TGP SINGLE RECORD - $scenarioDescription") {
        val token      = givenGetToken(isValid = true, identifier)
        println(token)
        val response   = removeTgpRecord(token, identifier, validRequestBody)
        val statusCode = response.getStatusCode
        statusCode.shouldBe(expectedStatusCode)
        val actualResponse = response.getBody.asString()
        assert(actualResponse.contains(expectedErrorMessage))
      }
    }

    Scenario(s"REMOVE TGP RECORD - Validate success 200 for Remove TGP record API") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = removeTgpRecord(token, "GB123456789001", validRequestBody)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(200)

    }

    Scenario(s"REMOVE TGP RECORD -Validate invalid response 400 with no payload for Remove TGP record API") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = removeTgpRecord(token, "GB123456789002", emptyRequestBody)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(400)

    }

    Scenario(s"REMOVE TGP RECORD - Validate invalid response 403 with invalid token for Remove TGP record API") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = removeTgpRecord(token, "GB123456789002", validRequestBody)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(403)

    }

    Scenario(s"REMOVE TGP RECORD - Validate invalid response 400 with invalid payload for Remove TGP record API") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = removeTgpRecord(token, "GB123456789001", invalidRequestBody)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(400)

    }

  }
}
