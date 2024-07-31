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

package uk.gov.hmrc.tgp.tests.specs.GetMultipleRecords

import org.scalatest.Tag
import play.api.test.Helpers.await
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.getResponseJsonFileAsString
import uk.gov.hmrc.tgp.tests.utils.TokenGenerator.generateRandomBearerToken

class TradersGoodProfileInvalidGetMultipleApiRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GetMultipleApiRecord
      extends Tag("uk.gov.hmrc.tgp.tests.specs.InvalidGetTradersGoodProfileGetMultipleApiRecordsSpec")

  private val FolderName       = "GetAPI"
  private val baseUrlForErrors = "/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=0&size=5"
  // private val baseUrlForMax    = "/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=0&size=501"
  val ValidEori                = "GB123456789001"
  val InvalidEori              = "GB123456789002"

  Scenario("Validate record not found response 405 for GET TGP record API") {
    val token    = givenGetToken(isValid = true, "GB123456789008")
    val response =
      getMultipleTgpRecord(token, s"GB123456789008/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=0&size=5")

    val statusCode = response.getStatusCode

    statusCode.shouldBe(405)

    val actualResponse   = response.getBody.asString()
    val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Get_405")
    assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
  }

  Scenario("Validate record not found response 404 for GET TGP record API") {
    val token    = givenGetToken(isValid = true, "GB123456789007")
    val response =
      getMultipleTgpRecord(token, s"GB123456789007/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=0&size=5")

    val statusCode = response.getStatusCode

    statusCode.shouldBe(404)

    val actualResponse   = response.getBody.asString()
    val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Get_404")
    assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
  }

  Scenario("Validate invalid token bearer response 401 for GET TGP record API") {
    val token      = generateRandomBearerToken()
    val response   = getMultipleTgpRecord(token, s"$ValidEori$baseUrlForErrors")
    val statusCode = response.getStatusCode

    statusCode.shouldBe(401)

    val actualResponse   = response.getBody.asString()
    val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Get_401")

    assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
  }

  Scenario("Validate invalid EORI no response 403 for GET TGP record API") {
    val token      = givenGetToken(isValid = true, ValidEori)
    val response   = getMultipleTgpRecord(token, s"$InvalidEori$baseUrlForErrors")
    val statusCode = response.getStatusCode

    statusCode.shouldBe(403)

    val actualResponse   = response.getBody.asString()
    val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Get_403")
    assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
  }

  Scenario("Validate message 'max allowed size is : 500' with error 400 for GET TGP record API") {
    val token = givenGetToken(isValid = true, "GB123456789003")

    lazy val response =
      getMultipleTgpRecord(token, s"GB123456789003/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=0&size=501")

    val statusCode = response.getStatusCode
    print(statusCode)

    statusCode.shouldBe(400)

    val actualResponse   = response.getBody.asString()
    val expectedResponse = getResponseJsonFileAsString(FolderName, "Scenario_Get_Multiple_400")
    assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
  }

}
