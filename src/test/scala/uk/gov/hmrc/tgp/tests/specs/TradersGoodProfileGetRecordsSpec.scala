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

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.getResponseJsonFileAsString

class TradersGoodProfileGetRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GET extends Tag("uk.gov.hmrc.tgp.tests.specs.GetTradersGoodProfileSpec")
  val identifier = "GB123456789001"
  Feature("Traders Good Profile Confirm EORI and TGP Enrollment API functionality") {
    Scenario("GET TGP SINGLE RECORD - Validate success response 200 for GET TGP record API") {
      val token      = givenGetToken(isValid = true, identifier)
      println(token)
      val response   = getTgpRecord(token, identifier)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(200)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString("Scenario_Get_200")
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")

    }
  }
}
