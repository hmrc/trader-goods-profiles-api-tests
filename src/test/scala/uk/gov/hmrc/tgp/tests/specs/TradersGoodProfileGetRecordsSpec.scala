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

import org.junit.Assert.assertEquals
import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient

class TradersGoodProfileGetRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GET extends Tag("uk.gov.hmrc.tgp.tests.specs.GetTradersGoodProfileSpec")
  val identifier = "GB123456789001"
  Feature("Traders Good Profile Confirm EORI and TGP Enrollment API functionality") {
    Scenario("GET - Validate success response 200 for GET TGP record API") {
      val token        = givenGetToken(isValid = true, identifier)
      println(token)
      val response     = getTgpRecord(token, identifier)
      val responseBody = response.getBody.asString
      val statusCode   = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      System.out.println("Response body: " + responseBody)
      statusCode.shouldBe(200)

    }
  }
}
