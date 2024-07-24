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

import play.api.libs.json.{JsObject, Json}
import io.restassured.response.Response
import io.restassured.specification.RequestSpecification
import uk.gov.hmrc.tgp.tests.client.{HttpClient, RestAssured}

trait CommonSpec extends BaseSpec with HttpClient with RestAssured {
  val requestSpecification: RequestSpecification = getRequestSpec

  val recordId = "b2fa315b-2d31-4629-90fc-a7b1a5119873"
  val actorId  = "GB123456789001"

  def givenGetToken(isValid: Boolean, identifier: String = ""): String = {
    Given(s"I receive a token $isValid")
    if (isValid) {
      authHelper.getAuthBearerToken(identifier)
    } else {
      identifier
    }
  }

  def setHeaders(request: RequestSpecification): RequestSpecification =
    request
      .header("X-Client-Id", "1234")
      .header("Content-Type", "application/json")
      .header("Accept", "application/vnd.hmrc.1.0+json")

  def setHeadersWithoutContentType(request: RequestSpecification): RequestSpecification =
    request
      .header("X-Client-Id", "1234")
      .header("Accept", "application/vnd.hmrc.1.0+json")

  def getTgpRecord(token: String, identifier: String): Response = {
    When(s"I get Get Tgp Records request without query params and receive a response")
    setHeadersWithoutContentType(requestSpecification)
      .header("Authorization", token)
      .when()
      .get(url + s"$identifier/records/$recordId")
      .andReturn()
  }

  def getMultipleTgpRecord(token: String, uri: String): Response = {
    When(s"I get Get Tgp Records request without query params and receive a response")
    setHeadersWithoutContentType(requestSpecification)
      .header("Authorization", token)
      .when()
      .get(url + uri)
      .andReturn()
  }

  def removeTgpRecord(token: String, identifier: String): Response = {
    When(s"I remove Tgp Records request and receive a response")
    setHeadersWithoutContentType(requestSpecification)
      .header("Authorization", token)
      .when()
      .delete(url + s"$identifier/records/$recordId?actorId=$actorId")
      .andReturn()
  }

  def createTgpRecord(token: String, identifier: String, request: String): Response = {
    When(s"I create Tgp Records request and receive a response")
    setHeaders(requestSpecification)
      .header("Authorization", token)
      .when()
      .body(request)
      .post(url + s"$identifier/records")
      .andReturn()
  }

  def updateTgpRecord(token: String, identifier: String, request: String): Response = {
    When(s"I Update Tgp Records request without query params and receive a response")
    setHeaders(requestSpecification)
      .header("Authorization", token)
      .when()
      .body(request)
      .patch(url + s"$identifier/records/$recordId")
      .andReturn()
  }

  def withdrawAdviceRecords(token: String, identifier: String, request: String, record: String): Response = {
    When(s"I Withdraw Advice Tgp Records request without query params and receive a response")
    setHeaders(requestSpecification)
      .header("Authorization", token)
      .when()
      .body(request)
      .put(url + s"$identifier/records/$record/advice")
      .andReturn()
  }

  def requestAdvice(token: String, identifier: String, request: String): Response = {
    When(s"I Request Advice API Tgp Records and receive a response")
    setHeaders(requestSpecification)
      .header("Authorization", token)
      .when()
      .body(request)
      .post(url + s"$identifier/records/$recordId/advice")
      .andReturn()
  }

  def maintainRecord(token: String, identifier: String, request: String): Response = {
    When(s"I Maintain API Tgp Records and receive a response")
    setHeaders(requestSpecification)
      .header("Authorization", token)
      .when()
      .body(request)
      .put(url + s"$identifier")
      .andReturn()
  }

  def compareJson(json1: String, json2: String): Boolean = {
    // Parse the JSON strings
    val parsedJson1 = Json.parse(json1)
    val parsedJson2 = Json.parse(json2)

    // Remove the correlationId and recordId fields from both JSON objects
    val jsonWithoutCorrelationId1 = parsedJson1.as[JsObject] - "correlationId" - "recordId"
    val jsonWithoutCorrelationId2 = parsedJson2.as[JsObject] - "correlationId" - "recordId"

    // Serialize the modified JSON objects back to strings for comparison
    val modifiedJson1 = Json.stringify(jsonWithoutCorrelationId1)
    val modifiedJson2 = Json.stringify(jsonWithoutCorrelationId2)
    // Compare the modified JSON strings
    modifiedJson1 == modifiedJson2
  }

}
