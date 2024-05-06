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

package uk.gov.hmrc.tgp.tests.common

import io.restassured.RestAssured.`given`
import io.restassured.http.Cookies
import io.restassured.path.json.JsonPath
import io.restassured.response.Response

import java.io.InputStream
import scala.io.Source
import scala.util.matching.Regex

trait EnvironmentHelpers {

  implicit class ResponseHelper(val value: Response) {

    def performRedirect(prepend: String = ""): Response =
      given().cookies(value.cookies()).redirects().follow(false).get(prepend + value.header("location"))

    def extractToken(pattern: Regex): Option[String] = thenExtractToken(pattern)

    def extractTokenAndCookies(pattern: Regex): Option[(String, Cookies)] =
      extractToken(pattern).map(Tuple2(_, value.detailedCookies()))

    def thenExtractToken(pattern: Regex): Option[String] =
      value
        .`then`()
        .assertThat()
        .statusCode(200)
        .extract()
        .body()
        .asInputStream()
        .asSource()
        .getLines()
        .collectFirst { case pattern(x) =>
          x
        }

  }

  implicit class InputStreamHelper(val value: InputStream) {
    def asSource(): Source = Source.fromInputStream(value)
  }

  implicit class JsonPathHelper(val value: JsonPath) {
    def getOption[A](path: String): Option[A] = Option(value.get[A](path))
  }

}
