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

package uk.gov.hmrc.tgp.tests.specs.GetSingleRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.getResponseJsonFileAsString

class TradersGoodProfileValidGetSingleApiRecordsSpec extends BaseSpec with CommonSpec with HttpClient {

  object GetSingleApiRecord extends Tag("uk.gov.hmrc.tgp.tests.specs.ValidGetTradersGoodProfileGetSingleApiRecordsSpec")

  Feature("Traders Good Profile Confirm EORI and TGP Enrollment API functionality for Valid GET API call") {

    val identifier           = "GB123456789001"
    val expectedStatusCode   = 200
    val expectedResponseFile = "Scenario_Get_Single_200"
    val scenarioDescription  = "Validate success response 200 for GET TGP record API"
    val FolderName           = "GetAPI"

    Scenario(s"GET TGP SINGLE RECORD - $scenarioDescription") {
      val token      = givenGetToken(isValid = true, identifier)
      println(token)
      val response   = getTgpRecord(token, identifier)
      val statusCode = response.getStatusCode
      System.out.println("Status code: " + statusCode)
      statusCode.shouldBe(expectedStatusCode)
      val actualResponse   = response.getBody.asString()
      val expectedResponse = getResponseJsonFileAsString(FolderName, expectedResponseFile)
      assert(compareJson(actualResponse, expectedResponse), "JSON response doesn't match the expected response.")
    }

  }
}
