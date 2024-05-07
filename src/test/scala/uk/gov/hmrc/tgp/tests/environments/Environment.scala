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

package uk.gov.hmrc.tgp.tests.environments

import cats.syntax.all._

import io.restassured.RestAssured.`given`
import io.restassured.http.Cookies

import uk.gov.hmrc.tgp.tests.common.EnvironmentHelpers
import uk.gov.hmrc.tgp.tests.common.InputData

import java.net.URLEncoder
import scala.collection.JavaConverters._
import scala.util.{Random, Try}
import scala.util.control.NonFatal

object Environment {

  val environments: Map[String, Environment] = Map[String, Environment](
    "local"        -> Local,
    "development"  -> Development,
    "qa"           -> QA,
    "staging"      -> Staging,
    "externaltest" -> ExternalTest
  )

  def modernEnrolment(eori: String): Map[String, String] = Map(
    "enrolment[0].state"                  -> "Activated",
    "enrolment[0].name"                   -> "HMRC-CUS-ORG",
    "enrolment[0].taxIdentifier[0].name"  -> "tgpFakeIdentifier",
    "enrolment[0].taxIdentifier[0].value" -> eori
  )

}

sealed trait Environment extends EnvironmentHelpers {

  def baseUrl: String

  def testSupportUrl: Option[String]

  def supportsObjectStore: Boolean

  final lazy val hasTestSupport = testSupportUrl.isDefined

  def authenticationResult: AuthenticationResult

  def authenticateWithRandomEori(): AuthenticationResult

  def authenticateWithEori(eori: String): AuthenticationResult

}

case object Local extends Environment {
  override val baseUrl: String = "http://localhost:10902"

  override val testSupportUrl: Option[String] = Some("http://localhost:9497")

  override val supportsObjectStore = true

  private val authUrl: String         = "http://localhost:9949/auth-login-stub/gg-sign-in"
  private val authRedirectUrl: String = "http://localhost:9949/auth-login-stub/session"

  private val bearerTokenPattern = raw".*>(.*Bearer .+)</code>.*".r

  lazy val authenticationResult: AuthenticationResult =
    generateAuthenticationDetails(Environment.modernEnrolment(InputData.eori), InputData.eori)

  def authenticateWithRandomEori(): AuthenticationResult = {
    val random10DigitNumber = Random.nextInt(1000000000).toString.reverse.padTo(10, '0').reverse
    val eori                = s"GB$random10DigitNumber"
    print(eori)
    generateAuthenticationDetails(Environment.modernEnrolment(eori), eori)
  }

  def authenticateWithEori(eori: String): AuthenticationResult =
    generateAuthenticationDetails(Environment.modernEnrolment(eori), eori)

  private def generateAuthenticationDetails(
    additionalEntries: Map[String, String],
    eori: String
  ): AuthenticationResult = {

    val details = for {
      bearerToken <- generateToken(additionalEntries)

    } yield AuthenticationDetails(bearerToken, None, None)

    AuthenticationResult(details, eori)

  }

  private def generateToken(additionalEntries: Map[String, String]): Either[AuthenticationFailure, String] =
    Try {
      val authHeader = Map("Content-Type" -> "application/x-www-form-urlencoded").asJava

      val authBody = (Map(
        "authorityId"                  -> "",
        "redirectionUrl"               -> authRedirectUrl,
        "credentialStrength"           -> "strong",
        "confidenceLevel"              -> "50",
        "affinityGroup"                -> "Individual",
        "email"                        -> "user@test.com",
        "credentialRole"               -> "User",
        "additionalInfo.emailVerified" -> "N/A"
      ) ++ additionalEntries).asJava

      val authResponse = given()
        .headers(authHeader)
        .formParams(authBody)
        .redirects()
        .follow(false)
        .when()
        .post(authUrl)

      val bearerToken = `given`()
        .headers(authHeader)
        .cookies(authResponse.cookies())
        .when()
        .get(authRedirectUrl)
        .thenExtractToken(bearerTokenPattern)

      println(s"Bearer Token: $bearerToken") // Print bearer token to console

      bearerToken
    }.toEither
      .leftMap { case NonFatal(x) =>
        AuthenticationFailure("Failed to authenticate", Some(x))
      }
      .flatMap(_.toRight(AuthenticationFailure("Failed to extract bearer token", None)))

}

sealed trait RemoteEnvironment extends Environment {

  def authUrl: String

  def apiAuthUrl: String

  def scopes: Seq[String]

  override val supportsObjectStore = true
  private lazy val joinedScoped    = URLEncoder.encode(scopes.mkString(" "), "UTF-8")

  lazy val authenticationResult: AuthenticationResult =
    authenticate(
      sys.props("clientId"),
      sys.props("clientSecret"),
      Environment.modernEnrolment(InputData.eori),
      InputData.eori
    )

  def authenticateWithRandomEori(): AuthenticationResult = {
    val eori = Random.alphanumeric.take(17).mkString
    authenticate(
      sys.props("clientId"),
      sys.props("clientSecret"),
      Environment.modernEnrolment(eori),
      eori
    )
  }

  def authenticateWithEori(eori: String): AuthenticationResult =
    authenticate(
      sys.props("clientId"),
      sys.props("clientSecret"),
      Environment.modernEnrolment(eori),
      eori
    )

  private lazy val authHeader = Map("Content-Type" -> "application/x-www-form-urlencoded").asJava

  private def authId(
    clientId: String,
    additionalEntries: Map[String, String]
  ): Either[AuthenticationFailure, (String, Cookies)] = {

    val authIdPattern   = raw".*auth_id=([0-9a-f]+).*".r
    val authRedirectUrl =
      s"$authUrl/oauth/authorize?client_id=$clientId&redirect_uri=urn:ietf:wg:oauth:2.0:oob&scope=$joinedScoped&response_type=code"
    val authBody        = (Map(
      "authorityId"                  -> "ABCD",
      "redirectionUrl"               -> authRedirectUrl,
      "credentialStrength"           -> "strong",
      "confidenceLevel"              -> "200",
      "affinityGroup"                -> "Individual",
      "nino"                         -> "AA000000A",
      "email"                        -> "user@test.com",
      "credentialRole"               -> "User",
      "affinityGroup"                -> "Individual",
      "additionalInfo.emailVerified" -> "N/A"
    ) ++ additionalEntries).asJava

    val firstResponse = given()
      .headers(authHeader)
      .formParams(authBody)
      .redirects()
      .follow(false)
      .when()
      .post(s"$authUrl/auth-login-stub/gg-sign-in")
      .performRedirect() // first redirect has the full URL

    // if we have a 400, check the json message
    if (firstResponse.statusCode() > 399) {
      val errorDescription = firstResponse.jsonPath().get[String]("error_description")
      Left(
        AuthenticationFailure(
          s"Status code ${firstResponse.statusCode()} was returned - make sure the redirect URI for the application is set to 'urn:ietf:wg:oauth:2.0:oob' in DevHub! Returned message was: $errorDescription",
          None
        )
      )
    } else {
      firstResponse
        .performRedirect(authUrl) // second doesn't
        .extractTokenAndCookies(authIdPattern)
        .toRight(AuthenticationFailure("Failed to obtain the auth ID", None))
    }
  }

  private def grantScope(cookies: Cookies, authId: String) = {
    val csrfTokenPattern = raw""".*name="csrfToken" value="([0-9a-f\-]+)".*""".r

    given()
      .headers(authHeader)
      .cookies(cookies)
      .when()
      .get(s"$authUrl/oauth/grantscope?auth_id=$authId")
      .extractTokenAndCookies(csrfTokenPattern)
      .toRight(AuthenticationFailure("Failed to obtain the CSRF token", None))
  }

  private def successToken(cookies: Cookies, authId: String, csrfToken: String) = {
    val successCodePattern = raw""".*<title>Success code=([a-f0-9]+)</title>.*""".r

    given()
      .headers(authHeader)
      .cookies(cookies)
      .formParam("csrfToken", csrfToken)
      .formParam("auth_id", authId)
      .when()
      .post(s"$authUrl/oauth/grantscope")
      .thenExtractToken(successCodePattern)
      .toRight(AuthenticationFailure("Failed to obtain success token", None))
  }

  private def accessCode(successCode: String, clientId: String, clientSecret: String) =
    given()
      .headers(authHeader)
      .formParam("code", successCode)
      .formParam("client_id", clientId)
      .formParam("client_secret", clientSecret)
      .formParam("grant_type", "authorization_code")
      .formParam("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
      .when()
      .post(apiAuthUrl)
      .jsonPath()
      .getOption[String]("access_token")
      .map(accessCodeJson => AuthenticationDetails(s"Bearer $accessCodeJson"))
      .toRight(AuthenticationFailure("Failed to obtain access token", None))

  protected def authenticate(
    clientId: String,
    clientSecret: String,
    additionalEntries: Map[String, String],
    eori: String
  ): AuthenticationResult =
    Try {
      for {
        auth      <- authId(clientId, additionalEntries)
        csrfToken <- grantScope(auth._2, auth._1)
        token     <- successToken(csrfToken._2, auth._1, csrfToken._1)
        code      <- accessCode(token, clientId, clientSecret)
      } yield code
    }.recover { case NonFatal(x) =>
      Left(AuthenticationFailure("Failed to authenticate", Some(x)))
    }.map(x => AuthenticationResult(x, eori))
      .get

}

case object Development extends RemoteEnvironment {
  override val apiAuthUrl: String             = "https://api.development.tax.service.gov.uk/oauth/token"
  override val authUrl: String                = "https://www.development.tax.service.gov.uk"
  override val testSupportUrl: Option[String] = None
  override val baseUrl: String                =
    "https://api.development.tax.service.gov.uk/customs/traders/goods-profiles/EORI/records"
  override val supportsObjectStore            = false

  override val scopes: Seq[String] = Seq(
    ""
  )
}

case object QA extends RemoteEnvironment {
  override val apiAuthUrl: String             = "https://api.qa.tax.service.gov.uk/oauth/token"
  override val baseUrl: String                = "https://api.qa.tax.service.gov.uk/customs/traders/goods-profiles/EORI/records"
  override val authUrl: String                = "https://www.qa.tax.service.gov.uk"
  override val scopes: Seq[String]            = Seq("")
  override val testSupportUrl: Option[String] = None
}

case object Staging extends RemoteEnvironment {
  override val apiAuthUrl: String             = "https://api.staging.tax.service.gov.uk/oauth/token"
  override val baseUrl: String                = "https://api.staging.tax.service.gov.uk/customs/traders/goods-profiles/EORI/records"
  override val testSupportUrl: Option[String] = None
  override val authUrl: String                = "https://www.staging.tax.service.gov.uk"
  override val scopes: Seq[String]            =
    Seq("")
}

case object ExternalTest extends RemoteEnvironment {
  override val apiAuthUrl: String             = "https://test-api.service.hmrc.gov.uk/oauth/token"
  override val baseUrl: String                = "https://test-api.service.hmrc.gov.uk/customs/traders/goods-profiles/EORI/records"
  override val testSupportUrl: Option[String] = None
  override val authUrl: String                = "https://test-www.tax.service.gov.uk"
  override val scopes: Seq[String]            = Seq("")
}
