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

package uk.gov.hmrc.tgp.tests.specs.RequestAdviceSpec

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.{getRequestJsonFileAsString, getResponseJsonFileAsString}

class TradersGoodProfileInvalidRequestAdviceSpec extends BaseSpec with CommonSpec with HttpClient {

  object RequestAdviceAPI extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileInvalidRequestAdviceSpec")

  Feature("Traders Good Profile API functionality to Invalid Request Advice API call") {
    val Eori1     = "GB123456789011"
    val Eori2     = "GB123456789012"
    val scenarios = List(
      (
        "GB123456789013",
        500,
        "Internal Error Response",
        "Validate the error 'Internal Error Response' 500 for Request Advice API call"
      ),
      (
        "GB123456789014",
        500,
        "Unauthorized",
        "Validate the error 'Unauthorized' 500 for Request Advice API call"
      ),
      (
        "GB123456789015",
        500,
        "Internal Server Error",
        "Validate the error 'internal server error' response 500 for Request Advice API call"
      ),
      (
        "GB123456789016",
        500,
        "Service Unavailable",
        "Validate the error 'Service Unavailable' 500 for Request Advice API call"
      ),
      ("GB123456789017", 500, "Bad Gateway", "Validate the error 'Bad Gateway' 500 for Request Advice API call")
    )

    val FolderName = "RequestAdviceAPI"

    var ValidPayload = "Scenario_Create_201"
    ValidPayload = getRequestJsonFileAsString(FolderName, ValidPayload)

    var PayloadWithoutActorId = "Scenario_Create_400"
    PayloadWithoutActorId = getRequestJsonFileAsString(FolderName, PayloadWithoutActorId)

    scenarios.foreach { case (identifier, expectedStatusCode, expectedErrorMessage, scenarioDescription) =>
      Scenario(s"Request Advice API - $scenarioDescription") {
        val token      = givenGetToken(isValid = true, identifier)
        val response   = requestAdvice(token, identifier, ValidPayload)
        val statusCode = response.getStatusCode
        statusCode.shouldBe(expectedStatusCode)
        val actualResponse = response.getBody.asString()
        System.out.println("response: " + actualResponse)
        assert(actualResponse.contains(expectedErrorMessage))
      }
    }

    Scenario(
      s"Request Advice API - Validate Invalid request parameter (actorId) response 400 for Request Advice API"
    ) {
      val token      = givenGetToken(isValid = true, Eori1)
      val response   = requestAdvice(token, Eori1, PayloadWithoutActorId)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(400)
      val actualResponse = response.getBody.asString()
      assert(actualResponse.contains("Mandatory field actorId was missing from body or is in the wrong format"))

    }

    Scenario(s"Request Advice API - Validate Forbidden response 403 for Request Advice API") {
      val token      = givenGetToken(isValid = true, Eori1)
      val response   = requestAdvice(token, "GB123456789001", ValidPayload)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(403)
      val actualResponse = response.getBody.asString()
      assert(actualResponse.contains("EORI number is incorrect"))
    }

    Scenario(
      s"Request Advice API - Validate response 400 with multiple error message for Request Advice API"
    ) {
      val token      = givenGetToken(isValid = true, Eori2)
      val response   = requestAdvice(token, Eori2, ValidPayload)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(400)
      val actualResponse   = response.getBody.asString()
      System.out.println("Status code: " + actualResponse)
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Create_400")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")

    }

  }
}
