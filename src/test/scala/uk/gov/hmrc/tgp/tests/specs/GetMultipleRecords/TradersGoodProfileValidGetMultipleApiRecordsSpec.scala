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
import play.api.libs.json.{JsObject, Json}
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}

import uk.gov.hmrc.tgp.tests.utils.ResponseUtils.{validateAdviceAndLockedStatus, validateToReviewAndReviewReason}

class TradersGoodProfileValidGetMultipleApiRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GetMultipleApiRecord
      extends Tag("uk.gov.hmrc.tgp.tests.specs.ValidGetTradersGoodProfileGetMultipleApiRecordsSpec")

  private val baseUrlForErrors = "/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=0&size=5"
  val ValidEori                = "GB123456789001"

  private def runScenario(
    identifier: String,
    expectedStatusCode: Int,
    scenarioDescription: String
  ): Unit =
    Scenario(s"$scenarioDescription for URL $identifier$baseUrlForErrors") {
      val token    = givenGetToken(isValid = true, identifier)
      val response = getMultipleTgpRecord(token, s"$identifier$baseUrlForErrors")

      val statusCode = response.getStatusCode
      statusCode.shouldBe(expectedStatusCode)

    }

  private def runSuccessfulScenario(scenarioDescription: String, url: String): Unit =
    Scenario(s"Validate $scenarioDescription") {
      val token    = givenGetToken(isValid = true, ValidEori)
      val response = getMultipleTgpRecord(token, url)

      val statusCode = response.getStatusCode
      statusCode.shouldBe(200)

    }

  private def runStatusScenario(adviceStatus: String, expectedLocked: Boolean): Unit = {
    val scenarioDescription = s"Validate GET TGP record API response for adviceStatus '$adviceStatus'"
    Scenario(scenarioDescription) {
      val urlParams = s"$ValidEori/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=0&size=7"
      val token     = givenGetToken(isValid = true, ValidEori)
      val response  = getMultipleTgpRecord(token, urlParams)

      val statusCode = response.getStatusCode
      statusCode.shouldBe(200)

      val actualResponse = response.getBody.asString()

      val actualJson = Json.parse(actualResponse)

      val records = (actualJson \ "records").as[List[JsObject]]

      val foundStatus = validateAdviceAndLockedStatus(records, adviceStatus, expectedLocked)
      foundStatus.shouldBe(true)

    }
  }

  Feature(
    "Traders Good Profile Confirm EORI and TGP Enrollment API functionality for Valid GET Multiple API Records call"
  ) {

    val scenarios = List(
      (
        "GB123456789003",
        200,
        "Scenario_Get_Multiple_ZeroRecord_200",
        "Validate Zero record found response 200 for GET TGP record API"
      )
    )

    scenarios.foreach { case (identifier, expectedStatusCode, expectedResponseFile, scenarioDescription) =>
      runScenario(identifier, expectedStatusCode, scenarioDescription)
    }

    val successfulScenarios = List(
      (
        "Scenario_Get_Multiple_UpdatedLastDate&Size_200",
        s"$ValidEori/records?lastUpdatedDate=2024-03-26T16:14:52Z&size=5"
      ),
      (
        "Scenario_Get_Multiple_Page&UpdatedLastDate_200",
        s"$ValidEori/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=0"
      ),
      (
        "Scenario_Get_Multiple_Page&UpdatedLastDate&Size_200",
        s"$ValidEori/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=0&size=5"
      ),
      ("Scenario_Get_Multiple_UpdatedLastDate_200", s"$ValidEori/records?lastUpdatedDate=2024-03-26T16:14:52Z"),
      ("Scenario_Get_Multiple_Size_200", s"$ValidEori/records?size=6"),
      ("Scenario_Get_Multiple_Page_200", s"$ValidEori/records?page=1"),
      ("Scenario_Get_Multiple_Page&Size_200", s"$ValidEori/records?page=0&size=4"),
      ("Scenario_Get_Multiple_Page&MaxSize_200", s"$ValidEori/records?page=0&size=500")
    )

    successfulScenarios.foreach { case (scenarioDescription, url) =>
      runSuccessfulScenario(scenarioDescription, url)
    }

    val statusScenarios = List(
      ("Requested", true),
      ("In progress", true),
      ("Information Requested", true),
      ("Not Requested", false),
      ("Withdrawn", false)
    )

    statusScenarios.foreach { case (adviceStatus, expectedLocked) =>
      runStatusScenario(adviceStatus, expectedLocked)
    }

    Scenario("Validate GET TGP record API response for toReview and reviewReason") {
      val urlParams = s"$ValidEori/records?lastUpdatedDate=2024-03-26T16:14:52Z&page=0&size=7"
      val token     = givenGetToken(isValid = true, ValidEori)
      val response  = getMultipleTgpRecord(token, urlParams)

      val statusCode = response.getStatusCode

      statusCode.shouldBe(200)

      val actualResponse = response.getBody.asString()

      val actualJson = Json.parse(actualResponse)

      val records = (actualJson \ "records").as[List[JsObject]]

      val foundReview = validateToReviewAndReviewReason(records)
      foundReview.shouldBe(true)

    }

  }

}
