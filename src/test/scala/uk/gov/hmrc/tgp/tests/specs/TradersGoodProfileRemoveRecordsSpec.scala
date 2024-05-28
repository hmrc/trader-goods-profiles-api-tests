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

import uk.gov.hmrc.tgp.tests.utils.JsonUtils.getRequestJsonFileAsString

class TradersGoodProfileRemoveRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GetApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.GetTradersGoodProfileSpec")

  Feature("Traders Good Profile API functionality for Remove Record API call") {

    val scenarios   = List(
      ("GB123456789001", 200, "Validate success response 200 for Remove record API"),
      ("GB123456789002", 400, "Validate Invalid request parameter response 400 for Remove TGP record API"),
      ("GB123456789003", 400, "Validate Invalid request parameter (recordId) response 400 for Remove TGP record API"),
      ("GB123456789004", 400, "Validate Invalid request parameter (actorId) response 400 for Remove TGP record API"),
      ("GB123456789005", 500, "Validate internal server error response 500 for Remove record API"),
      ("GB123456789006", 404, "Validate invalid URL response 404 for Remove record API"),
      ("GB123456789007", 405, "Validate method not allowed response 405 for Remove record API")
    )
    val scenario572_ValidPayload = "RemoveWithInValidactorId"
    val requestBody = getRequestJsonFileAsString(scenario572_ValidPayload)

    val scenario572_EmptyPayload = "RemoveWithNoactorId"
    val requestBody1      = getRequestJsonFileAsString(scenario572_EmptyPayload)

    val scenario572_InvalidPayload = "RemoveWithNoactorId"
    val requestBody2 = getRequestJsonFileAsString(scenario572_InvalidPayload)

    scenarios.foreach { case (identifier, expectedStatusCode, scenarioDescription) =>
      Scenario(s"REMOVE TGP RECORD - $scenarioDescription") {
        val token      = givenGetToken(isValid = true, identifier)
        println(token)
        val response   = removeTgpRecord(token, identifier, requestBody)
        val statusCode = response.getStatusCode
        System.out.println("Status code: " + statusCode)
        statusCode.shouldBe(expectedStatusCode)
      }
    }

    Scenario(s"REMOVE TGP RECORD - Validate success 200 for Remove TGP record API") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = removeTgpRecord(token, "GB123456789001", requestBody)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(200)

    }


    Scenario(s"REMOVE TGP RECORD -Validate invalid response 400 with no payload for Remove TGP record API") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = removeTgpRecord(token, "GB123456789002", requestBody1)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(400)

    }

    Scenario(s"REMOVE TGP RECORD - Validate invalid no response 403 with invalid token for Remove TGP record API") {
      val token      = givenGetToken(isValid = true, "GB123456789001")
      val response   = removeTgpRecord(token, "GB123456789002", requestBody)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(403)

    }



  }
}
