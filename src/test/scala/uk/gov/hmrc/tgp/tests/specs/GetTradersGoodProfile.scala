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

import io.restassured.RestAssured.`given`
import org.scalatest.freespec.AnyFreeSpec
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.tgp.tests.{RegressionTestSuite, SmallMessageOnly, SmokeTestSuite, TestEnvironment}
import uk.gov.hmrc.tgp.tests.common.RestAssuredHelpers
import uk.gov.hmrc.tgp.tests.environments.AuthenticationResult

import scala.io.Source
import scala.util.Random

class GetTradersGoodProfile
    extends AnyFreeSpec
    with SmokeTestSuite
    with RegressionTestSuite
    with RestAssuredHelpers
    with SmallMessageOnly {
  def compareJson(json1: String, json2: String): Boolean = {
    // Parse the JSON strings
    val parsedJson1 = Json.parse(json1)
    val parsedJson2 = Json.parse(json2)

    // Remove the timestamp field from both JSON objects
    val jsonWithoutTimestamp1 = parsedJson1.as[JsObject] - "timestamp"
    val jsonWithoutTimestamp2 = parsedJson2.as[JsObject] - "timestamp"

    // Serialize the modified JSON objects back to strings for comparison
    val modifiedJson1 = Json.stringify(jsonWithoutTimestamp1)
    val modifiedJson2 = Json.stringify(jsonWithoutTimestamp2)

    // Compare the modified JSON strings
    modifiedJson1 == modifiedJson2
  }

  override lazy val authToken: AuthenticationResult = TestEnvironment.environment.authenticateWithRandomEori()
  "*FEATURE* -Confirm EORI and TGP Enrollment" - {
    "GET - Validate success response 200 for GET TGP record API " in {
      val enrollmentEORINumber = authToken.eori
      val expectedResponse     = Source.fromResource("Json.intermediateResponse/Scenario_Get_200.text").mkString
      val sut                  = given()
        .withVersionOneHeaders()
        .when()
        .get(
          s"${TestEnvironment.environment.baseUrl}/$enrollmentEORINumber/records?lastUpdatedDate=2024-11-18T23:20:19Z"
        )
      sut
        .`then`()
        .statusCode(200)
      val actualResponse       = sut.getBody().asString()
      assert(actualResponse == expectedResponse, "Response doesn't match the expected response.")

    }

    val randomToken = Random.alphanumeric.take(20).mkString

    "GET -Validate the status code 401 with invalid bearer token" in {
      val enrollmentEORINumber = authToken.eori
      val expectedResponse     = Source.fromResource("Json.intermediateResponse/Scenario_Get_401.json").mkString

      val sut            = given()
        .header("Authorization", s"Bearer $randomToken") // Pass the random token in the Authorization header
        .when()
        .get(
          s"${TestEnvironment.environment.baseUrl}/$enrollmentEORINumber/records?lastUpdatedDate=2024-11-18T23:20:19Z"
        )
      sut
        .`then`()
        .statusCode(401)
      val actualResponse = sut.getBody().asString()
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    "GET -Validate the status code 403 with invalid EORI number" in {
      val expectedResponse = Source.fromResource("Json.intermediateResponse/Scenario_Get_403.json").mkString
      val sut              = given()
        .withVersionOneHeaders()
        .when()
        .get(
          s"${TestEnvironment.environment.baseUrl}/GB8783/records?lastUpdatedDate=2024-11-18T23:20:19Z"
        )
      sut
        .`then`()
        .statusCode(403)
      val actualResponse   = sut.getBody().asString()
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }
  }

}
