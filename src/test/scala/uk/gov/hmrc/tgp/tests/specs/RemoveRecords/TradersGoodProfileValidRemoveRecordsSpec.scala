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

package uk.gov.hmrc.tgp.tests.specs.RemoveRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}

class TradersGoodProfileValidRemoveRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object RemoveApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileValidRemoveRecordsSpec")

  Feature("Traders Good Profile API functionality for Valid Remove Record API call") {

    val ValidEori = "GB123456789001"

    Scenario(s"REMOVE TGP RECORD - Validate valid response 204 with valid token for Remove TGP record API") {
      val token      = givenGetToken(isValid = true, ValidEori)
      val response   = removeTgpRecord(token, ValidEori)
      val statusCode = response.getStatusCode
      statusCode.shouldBe(204)
    }

  }
}
