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

package uk.gov.hmrc.tgp.tests.service

import play.api.libs.ws.StandaloneWSRequest
import uk.gov.hmrc.tgp.tests.client.HttpClient
import uk.gov.hmrc.tgp.tests.conf.TestConfiguration

import scala.concurrent.Await
import scala.concurrent.duration._

class AuthService extends HttpClient {

  val host: String        = TestConfiguration.url("auth")
  val redirectUrl: String = TestConfiguration.getConfigValue("redirect-url")

  def authPayload(identifier: String): String =
    s"""
       | {
       |    "authorityId": "Test",
       |    "redirectionUrl": "$redirectUrl",
       |    "excludeGnapToken": false,
       |    "credentialStrength": "strong",
       |    "confidenceLevel": 50,
       |    "affinityGroup": "Organisation",
       |    "email": "user@test.com",
       |    "credentialRole": "User",
       |    "additionalInfo.emailVerified": "N/A",
       |    "presets-dropdown": "IR-SA",
       |    "credId": "tgpTest",
       |    "enrolments": [
       |        {
       |            "key": "HMRC-CUS-ORG",
       |            "identifiers": [
       |                {
       |                    "key": "EORINumber",
       |                    "value": "$identifier"
       |                }
       |            ],
       |            "state": "Activated"
       |        }
       |    ]
       |}
    """.stripMargin

  def postLogin(identifier: String): StandaloneWSRequest#Self#Response = {
    val url = s"$host" + TestConfiguration.getConfigValue("auth-login-stub_uri")
    Await.result(post(url, authPayload(identifier), ("Content-Type", "application/json")), 10.seconds)
  }
}
