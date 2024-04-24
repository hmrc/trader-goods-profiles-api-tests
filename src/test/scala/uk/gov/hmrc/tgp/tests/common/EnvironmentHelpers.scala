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
        .collectFirst {
          case pattern(x) => x
        }

  }

  implicit class InputStreamHelper(val value: InputStream) {
    def asSource(): Source = Source.fromInputStream(value)
  }

  implicit class JsonPathHelper(val value: JsonPath) {
    def getOption[A](path: String): Option[A] = Option(value.get[A](path))
  }

}
