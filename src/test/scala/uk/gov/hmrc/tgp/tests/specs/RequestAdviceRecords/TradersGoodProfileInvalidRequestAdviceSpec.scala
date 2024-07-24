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

package uk.gov.hmrc.tgp.tests.specs.RequestAdviceRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.{getRequestJsonFileAsString, getResponseJsonFileAsString}

class TradersGoodProfileInvalidRequestAdviceSpec extends BaseSpec with CommonSpec with HttpClient {

  object RequestAdviceAPI extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileInvalidRequestAdviceSpec")

  Feature("Traders Good Profile API functionality to Invalid Request Advice API call") {
    val validEORI      = "GB123456789011"
    val invalidEORI    = "GB123456789012"
    val inprogressEORI = "GB123456789007"

    val FolderName = "RequestAdviceAPI"

    var ValidPayload = "Scenario_Create_201"
    ValidPayload = getRequestJsonFileAsString(FolderName, ValidPayload)

    var PayloadWithInvalidRequestorName = "Scenario_Create_400"
    PayloadWithInvalidRequestorName = getRequestJsonFileAsString(FolderName, PayloadWithInvalidRequestorName)

    var PayloadWithMissingAndIncorrectFormat = "Scenario_Create_400_WithMultipleErrors"
    PayloadWithMissingAndIncorrectFormat = getRequestJsonFileAsString(FolderName, PayloadWithMissingAndIncorrectFormat)

    Scenario(
      s"Request Advice API - Validate 400 response for Request Advice API with incorrect format fields"
    ) {
      val token      = givenGetToken(isValid = true, validEORI)
      val response   = requestAdvice(token, validEORI, PayloadWithInvalidRequestorName)
      val statusCode = response.getStatusCode
      statusCode.shouldBe(400)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Create_400")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")

    }

    Scenario(
      s"Request Advice API - Validate 400 response for Request Advice API with multiple error messages by Missing mandatory and incorrect format fields"
    ) {
      val token      = givenGetToken(isValid = true, validEORI)
      val response   = requestAdvice(token, validEORI, PayloadWithMissingAndIncorrectFormat)
      val statusCode = response.getStatusCode
      statusCode.shouldBe(400)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Create_400_WithMultipleErrors")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")

    }

    Scenario(s"Request Advice API - Validate Forbidden response 403 for Request Advice API") {
      val token      = givenGetToken(isValid = true, validEORI)
      val response   = requestAdvice(token, invalidEORI, ValidPayload)
      val statusCode = response.getStatusCode
      statusCode.shouldBe(403)
      val actualResponse = response.getBody.asString()
      assert(actualResponse.contains("EORI number is incorrect"))
    }

    Scenario(s"Request Advice API - Validate response 409 for Request Advice API") {
      val token      = givenGetToken(isValid = true, inprogressEORI)
      val response   = requestAdvice(token, inprogressEORI, ValidPayload)
      val statusCode = response.getStatusCode
      statusCode.shouldBe(409)
      val actualResponse = response.getBody.asString()
      assert(actualResponse.contains("There is an ongoing advice request and a new request cannot be requested"))
    }

  }
}
