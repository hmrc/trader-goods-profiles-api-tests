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

package uk.gov.hmrc.tgp.tests.specs.RemoveRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils

class TradersGoodProfileInvalidRemoveRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object RemoveApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileInvalidRemoveRecordsSpec")

  Feature("Traders Good Profile API functionality for Invalid Remove Record API call") {

    val FolderName  = "RemoveAPI"
    val ValidEori   = "GB123456789001"
    val InvalidEori = "GB123456789002"

    def validateScenario(
      scenarioDescription: String,
      identifier: String,
      expectedStatusCode: Int,
      payloadFileName: String,
      expectedErrorMessage: String = ""
    ): Unit =
      Scenario(s"REMOVE TGP SINGLE RECORD - $scenarioDescription") {
        val token      = givenGetToken(isValid = true, identifier)
        val payload    = JsonUtils.getRequestJsonFileAsString(FolderName, payloadFileName)
        val response   = removeTgpRecord(token, identifier, payload)
        val statusCode = response.getStatusCode
        System.out.println("Status code: " + statusCode)
        statusCode.shouldBe(expectedStatusCode)
        if (expectedErrorMessage.nonEmpty) {
          val actualResponse = response.getBody.asString()
          assert(actualResponse.contains(expectedErrorMessage))
        }
      }

    val scenarios = List(
      (
        "GB123456789002",
        400,
        "RemoveWithValidActorId",
        "Mandatory field eori was missing from body or is in the wrong format",
        "Validate Invalid request parameter (Mandatory field eori) response 400 for Remove TGP record API"
      ),
      (
        "GB123456789003",
        400,
        "RemoveWithInValidActorId",
        "The recordId has been provided in the wrong format",
        "Validate Invalid request parameter (recordId) response 400 for Remove TGP record API"
      ),
      (
        "GB123456789004",
        400,
        "RemoveWithNoActorId",
        "Mandatory field actorId was missing from body or is in the wrong format",
        "Validate Invalid request parameter (actorId) response 400 for Remove TGP record API"
      ),
      (
        "GB123456789005",
        500,
        "RemoveWithValidActorId",
        "Unauthorized",
        "Validate internal server error response 500 for Remove record API"
      ),
      (
        "GB123456789006",
        404,
        "RemoveWithValidActorId",
        "Not Found",
        "Validate method not found 404 for Remove record API"
      ),
      (
        "GB123456789007",
        405,
        "RemoveWithValidActorId",
        "Method Not Allowed",
        "Validate method not allowed response 405 for Remove record API"
      )
    )

    scenarios.foreach { case (identifier, expectedStatusCode, payloadFileName, _, scenarioDescription) =>
      validateScenario(scenarioDescription, identifier, expectedStatusCode, payloadFileName)
    }

    Scenario(s"REMOVE TGP RECORD - Validate invalid response 403 with invalid token for Remove TGP record API") {
      val token      = givenGetToken(isValid = true, ValidEori)
      val response   = removeTgpRecord(
        token,
        InvalidEori,
        JsonUtils.getRequestJsonFileAsString(FolderName, "RemoveWithValidActorId")
      )
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(403)
    }

    Scenario(s"REMOVE TGP RECORD - Validate invalid response 400 with invalid payload for Remove TGP record API") {
      val token      = givenGetToken(isValid = true, ValidEori)
      val response   = removeTgpRecord(
        token,
        ValidEori,
        JsonUtils.getRequestJsonFileAsString(FolderName, "RemoveWithInValidActorId")
      )
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(400)
    }

  }
}
