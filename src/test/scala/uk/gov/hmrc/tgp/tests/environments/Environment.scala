package uk.gov.hmrc.tgp.tests.environments

import uk.gov.hmrc.tgp.tests.common.EnvironmentHelpers

object Environment {

  val environments: Map[String, Environment] = Map[String, Environment](
    "local" -> Local
  )

}

sealed trait Environment extends EnvironmentHelpers {}

case object Local extends Environment {
  private val authUrl: String         = "http://localhost:9949/auth-login-stub/gg-sign-in"
  private val authRedirectUrl: String = "http://localhost:9949/auth-login-stub/session"
  private val bearerTokenPattern      = raw".*>(.*Bearer .+)</code>.*".r

}
