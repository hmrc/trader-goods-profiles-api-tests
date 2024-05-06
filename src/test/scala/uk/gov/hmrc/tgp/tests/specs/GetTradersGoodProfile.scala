

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
    "GET - status expected is 200" in {
      val enrollmentEORINumber = authToken.eori
      val expectedResponse     = Source.fromResource("Json.intermediateResponse/Scenario_Get_200.text").mkString
      val sut                  = given()
        .withVersionOneHeaders()
        .when()
        .get(
          s"${TestEnvironment.environment.baseUrl}/$enrollmentEORINumber/records?lastUpdatedDate=2024-11-18T23:20:19Z"
        )
      val actualResponse       = sut.getBody().asString()
      assert(actualResponse == expectedResponse, "Response doesn't match the expected response.")

    }

    val randomToken = Random.alphanumeric.take(20).mkString

    "GET - status expected is 401" in {
      val enrollmentEORINumber = authToken.eori
      val expectedResponse     = Source.fromResource("Json.intermediateResponse/Scenario_Get_401.json").mkString

      val sut            = given()
        .header("Authorization", s"Bearer $randomToken") // Pass the random token in the Authorization header
        .when()
        .get(
          s"${TestEnvironment.environment.baseUrl}/$enrollmentEORINumber/records"
        )
      sut
        .`then`()
        .statusCode(401)
      val actualResponse = sut.getBody().asString()
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

    "GET -  status expected is 403" in {
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
