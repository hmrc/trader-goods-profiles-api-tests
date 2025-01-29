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

package uk.gov.hmrc.tgp.tests.specs.RequestAdviceRecords

import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.specs.{BaseSpec, CommonSpec}
import uk.gov.hmrc.tgp.tests.utils.JsonUtils.getRequestJsonFileAsString

class TradersGoodProfileValidRequestAdviceSpec extends BaseSpec with CommonSpec with HttpClient {

  object RequestAdviceAPI extends Tag("uk.gov.hmrc.tgp.tests.specs.TradersGoodProfileValidRequestAdviceSpec")

  private val FolderName         = "RequestAdviceAPI"
  private val Identifier         = "GB123456789011787"
  private val ExpectedStatusCode = 201

  private def executeScenario(payloadFile: String): Unit = {
    val payload    = getRequestJsonFileAsString(FolderName, payloadFile)
    val token      = givenGetToken(isValid = true, Identifier)
    val response   = requestAdvice(token, Identifier, payload)
    val statusCode = response.getStatusCode
    statusCode.shouldBe(ExpectedStatusCode)

  }

  private def executeMinIdentifierScenario(minIdentifier: String): Unit = {
    val payload    = getRequestJsonFileAsString(FolderName, "Scenario_Create_201")
    val token      = givenGetToken(isValid = true, minIdentifier)
    val response   = requestAdvice(token, minIdentifier, payload)
    val statusCode = response.getStatusCode
    statusCode.shouldBe(ExpectedStatusCode)

  }

  Scenario(s"Request Advice API - Validate success response 201 for Valid Request Advice API call") {
    executeScenario("Scenario_Create_201")
  }

  Scenario(s"Request Advice API - Validate 201 Success Response with max values") {
    executeScenario("Scenario_Create_201_MaxLength")
  }

  Scenario(s"Request Advice API - Validate 201 Success Response with min values") {
    executeScenario("Scenario_Create_201_MinLength")
  }

  Scenario(s"Request Advice API - Validate 201 Success Response with minimum identifier value") {
    val minIdentifier = "GB123456778012"
    executeMinIdentifierScenario(minIdentifier)
  }
}
