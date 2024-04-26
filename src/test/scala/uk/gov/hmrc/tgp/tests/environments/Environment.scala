package uk.gov.hmrc.tgp.tests.environments

import cats.syntax.all._
import io.restassured.RestAssured.`given`
import io.restassured.http.Cookies

import uk.gov.hmrc.tgp.tests.common.EnvironmentHelpers

import scala.collection.JavaConverters._
import scala.util.Try
import scala.util.control.NonFatal

object Environment {

  val environments: Map[String, Environment] = Map[String, Environment](
    "local"       -> Local,
    "development" -> Development,
    "qa"          -> QA,
    "staging"     -> Staging
  )

}

sealed trait Environment extends EnvironmentHelpers {

  def baseUrl: String

}

case object Local extends Environment {
  override val baseUrl: String        = "http://localhost:10902"
  private val authUrl: String         = "http://localhost:9949/auth-login-stub/gg-sign-in"
  private val authRedirectUrl: String = "http://localhost:9949/auth-login-stub/session"
  private val bearerTokenPattern      = raw".*>(.*Bearer .+)</code>.*".r

  private def generateToken(identifier: String): Either[AuthenticationFailure, String] =
    Try {
      val authHeader = Map("Content-Type" -> "application/json").asJava
      val enrolments = Map(
        "enrolments" -> Seq(
          Map(
            "key"         -> "HMRC-CUS-ORG",
            "identifiers" -> Seq(
              Map(
                "key"   -> "tgpFakeIdentifier",
                "value" -> identifier
              )
            ),
            "state"       -> "Activated"
          )
        )
      )

      val authBody = (Map(
        "authorityId"                  -> "",
        "redirectionUrl"               -> authRedirectUrl,
        "credentialStrength"           -> "strong",
        "confidenceLevel"              -> "50",
        "affinityGroup"                -> "Individual",
        "email"                        -> "user@test.com",
        "credentialRole"               -> "User",
        "affinityGroup"                -> "Individual",
        "additionalInfo.emailVerified" -> "N/A"
      ) ++ enrolments).asJava

      val authResponse = given()
        .headers(authHeader)
        .formParams(authBody)
        .redirects()
        .follow(false)
        .when()
        .post(authUrl)

      `given`()
        .headers(authHeader)
        .cookies(authResponse.cookies())
        .when()
        .get(authRedirectUrl)
        .thenExtractToken(bearerTokenPattern)
    }.toEither
      .leftMap { case NonFatal(x) =>
        AuthenticationFailure("Failed to authenticate", Some(x))
      }
      .flatMap(_.toRight(AuthenticationFailure("Failed to extract bearer token", None)))

}

sealed trait RemoteEnvironment extends Environment {

  def authUrl: String

  def apiAuthUrl: String

  private lazy val authHeader = Map("Content-Type" -> "application/json").asJava

  private def authId(
    identifier: String,
    clientId: String,
    additionalEntries: Map[String, String]
  ): Either[AuthenticationFailure, (String, Cookies)] = {
    val enrolments      = Map(
      "enrolments" -> Seq(
        Map(
          "key"         -> "HMRC-CUS-ORG",
          "identifiers" -> Seq(
            Map(
              "key"   -> "",
              "value" -> identifier
            )
          ),
          "state"       -> "Activated"
        )
      )
    )
    val authIdPattern   = raw".*auth_id=([0-9a-f]+).*".r
    val authRedirectUrl = ""
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
    ) ++ enrolments).asJava

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

}

case object Development extends RemoteEnvironment {
  override val apiAuthUrl: String = "https://api.development.tax.service.gov.uk/oauth/token"
  override val authUrl: String    = "https://www.development.tax.service.gov.uk"
  override val baseUrl: String    = ""

}

case object QA extends RemoteEnvironment {
  override val apiAuthUrl: String = "https://api.qa.tax.service.gov.uk/oauth/token"
  override val baseUrl: String    = ""
  override val authUrl: String    = "https://www.qa.tax.service.gov.uk"

}

case object Staging extends RemoteEnvironment {
  override val apiAuthUrl: String = "https://api.staging.tax.service.gov.uk/oauth/token"
  override val baseUrl: String    = ""
  override val authUrl: String    = "https://www.staging.tax.service.gov.uk"

}
