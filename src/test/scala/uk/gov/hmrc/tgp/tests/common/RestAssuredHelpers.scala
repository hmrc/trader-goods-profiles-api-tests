

package uk.gov.hmrc.tgp.tests.common

import io.restassured.RestAssured
import io.restassured.config.RestAssuredConfig
import io.restassured.specification.RequestSpecification
import uk.gov.hmrc.tgp.tests.TestSuite
import uk.gov.hmrc.tgp.tests.environments.AuthenticationResult

object RestAssuredHelpers {

  lazy val config: RestAssuredConfig = {
    val ec = RestAssured.config.getEncoderConfig
    RestAssured.config.encoderConfig(ec.appendDefaultContentCharsetToContentTypeIfUndefined(false))
  }
}

trait RestAssuredHelpers extends EnvironmentHelpers { this: TestSuite =>

  implicit class RequestSpecificationHelper(val spec: RequestSpecification) {

    def withBasicHeaders(
      authenticationResult: AuthenticationResult = authToken,
      contentType: String = "application/json"
    ): RequestSpecification = {
      spec.config(RestAssuredHelpers.config)
      // We potentially throw to abort the test if this is invalid, but as this might be a different auth call to the main one, we
      // don't necessarily want to abort the tests (though we don't have the args so we can't do so from here anyway)
      val authResult = authenticationResult.getOrThrow
      // If we're on local, we will have a client ID. On remote, this will be populated by the platform
      authResult.clientId.foreach { c =>
        spec.header("X-Client-Id", c.value)
      }
      spec.header("Authorization", authResult.userBearerToken)
      spec.header("Content-Type", contentType)
    }

    def withVersionOneHeaders(): RequestSpecification = {
      withBasicHeaders()
      spec.header("Accept", "application/vnd.api+json")
    }

    def withVersionTwoHeaders(
      authenticationResult: AuthenticationResult = authToken,
      contentType: String = "application/xml",
      acceptHeader: String = "application/vnd.hmrc.2.0+json"
    ): RequestSpecification = {
      withBasicHeaders(authenticationResult, contentType)
      spec.header("Accept", acceptHeader)
    }

    def withInvalidAcceptHeader(): RequestSpecification = {
      withBasicHeaders()
      spec.header("Accept", "Rubiisijsdfnid")
    }
  }
}
