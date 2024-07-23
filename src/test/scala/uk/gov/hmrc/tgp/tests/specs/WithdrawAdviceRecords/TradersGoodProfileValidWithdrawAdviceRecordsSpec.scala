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
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.getRequestJsonFileAsString

class TradersGoodProfileValidWithdrawAdviceRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object UpdateApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileValidWithdrawAdviceRecordsSpec")

  Feature("Traders Good Profile API functionality for Valid Withdraw Advice Record API call") {

    val FolderName = "WithdrawAdviceAPI"
    val ValidEori  = "GB123456789011"

    val payloads = Map(
      "PayLoadNoBodyContent"      -> getRequestJsonFileAsString(FolderName, "Scenario_WithdrawAdvice_NoBodyContent_204"),
      "PayloadWithMaxFieldValues" -> getRequestJsonFileAsString(FolderName, "Scenario_WithdrawAdvice_MaxLength_204"),
      "PayloadWithMinFieldValues" -> getRequestJsonFileAsString(FolderName, "Scenario_WithdrawAdvice_MinLength_204")
    )

    def runScenario(
      identifier: String,
      expectedStatusCode: Int,
      payload: String,
      description: String
    ): Unit =
      Scenario(s"WITHDRAW ADVICE TGP RECORD - $description") {
        val token      = givenGetToken(isValid = true, identifier)
        val response   =
          withdrawAdviceRecords(token, identifier, payload, record = "8ebb6b04-6ab0-4fe2-ad62-e6389a8a204f")
        val statusCode = response.getStatusCode
        System.out.println("Status code: " + statusCode)
        statusCode.shouldBe(expectedStatusCode)
      }

    runScenario(
      ValidEori,
      204,
      payloads("PayLoadNoBodyContent"),
      "Validate success 204 for Withdraw Advice TGP record API"
    )
    runScenario(
      ValidEori,
      204,
      payloads("PayloadWithMaxFieldValues"),
      "Validate success 204 for Withdraw Advice TGP record API with all max values"
    )
    runScenario(
      ValidEori,
      204,
      payloads("PayloadWithMinFieldValues"),
      "Validate success 204 for Withdraw Advice TGP record API with all min values"
    )

  }
}
