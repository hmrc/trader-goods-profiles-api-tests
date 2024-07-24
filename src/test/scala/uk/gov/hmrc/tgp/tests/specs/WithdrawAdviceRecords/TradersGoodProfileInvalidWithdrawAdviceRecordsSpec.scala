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

package uk.gov.hmrc.tgp.tests.specs.WithdrawAdviceRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.{getRequestJsonFileAsString, getResponseJsonFileAsString}

class TradersGoodProfileInvalidWithdrawAdviceRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object UpdateApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileInvalidWithdrawAdviceRecordsSpec")

  Feature("Traders Good Profile API functionality for Invalid Withdraw Advice Record API call") {
    val ValidEori = "GB123456789011"

    val FolderName = "WithdrawAdviceAPI"
    val payloads   = Map(
      "PayLoadNoBodyContent" -> getRequestJsonFileAsString(FolderName, "Scenario_WithdrawAdvice_NoBodyContent_204")
    )

    def runScenario(
      identifier: String,
      expectedResponseFile: String,
      expectedStatusCode: Int,
      payload: String,
      description: String
    ): Unit =
      Scenario(s"WITHDRAW ADVICE TGP RECORD - $description") {
        val token      = givenGetToken(isValid = true, identifier)
        val response   =
          withdrawAdviceRecords(token, identifier, payload, record = "c5e0cb9f-2292-480b-8a43-4222db5c9c85")
        val statusCode = response.getStatusCode
        statusCode.shouldBe(expectedStatusCode)
        val actualResponse   = response.getBody.asString()
        val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
        assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
      }

    runScenario(
      ValidEori,
      "Scenario_WithdrawAdvice_BadRequest_Trader_400",
      400,
      payloads("PayLoadNoBodyContent"),
      "Validate error message 400 for Withdraw Advice TGP record API "
    )

  }
}
