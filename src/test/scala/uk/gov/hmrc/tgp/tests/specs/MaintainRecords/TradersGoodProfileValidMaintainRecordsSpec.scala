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

package uk.gov.hmrc.tgp.tests.specs.MaintainRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.{getRequestJsonFileAsString, getResponseJsonFileAsString}

class TradersGoodProfileValidMaintainRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object MaintainApiRecords extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileValidMaintainRecordsSpec")

  Feature("Traders Good Profile API functionality to Valid Maintain Records API call") {

    val FolderName = "MaintainRecordAPI"

    val scenarios = List(
      ("GB123456789001", 200, "Scenario_Maintain_200", "Validate success response 200 for Maintain Records API call"),
      (
        "GB123456789001",
        200,
        "Scenario_Maintain_OnlyMandatoryFields_200",
        "Validate success 200 for Maintain TGP record API with only Mandatory values"
      )
    )

    def getPayload(scenario: String): String = getRequestJsonFileAsString(FolderName, scenario)

    def executeScenario(
      identifier: String,
      expectedStatusCode: Int,
      expectedResponseFile: String,
      scenarioDescription: String
    ): Unit =
      Scenario(s"Maintain Records Api - $scenarioDescription") {
        val token      = givenGetToken(isValid = true, identifier)
        val response   = maintainRecord(token, identifier, getPayload(expectedResponseFile))
        val statusCode = response.getStatusCode
        statusCode.shouldBe(expectedStatusCode)
        val actualResponse   = response.getBody.asString()
        val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
        assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
      }

    scenarios.foreach { case (identifier, expectedStatusCode, expectedResponseFile, scenarioDescription) =>
      executeScenario(identifier, expectedStatusCode, expectedResponseFile, scenarioDescription)
    }

  }
}
