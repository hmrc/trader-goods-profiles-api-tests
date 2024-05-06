

package uk.gov.hmrc.tgp.tests

import uk.gov.hmrc.tgp.tests.environments.{AuthenticationResult, Development, Environment, Local}

object TestEnvironment {

  lazy val environment: Environment =
    sys.props.get("environment").map(_.toLowerCase).map(Environment.environments).getOrElse(Local)

  lazy val isLocal: Boolean        = environment == Local
  lazy val isDevelopment: Boolean  = environment == Development
  lazy val hasTestSupport: Boolean = environment.hasTestSupport
  lazy val hasObjectStore: Boolean = environment.supportsObjectStore

  lazy val authenticationDetails: AuthenticationResult =
    environment.authenticationResult

  lazy val isSmoke: Boolean                 = sys.props.contains("smoke")
  private lazy val isRegression0: Boolean   = sys.props.contains("regression")
  lazy val isFunctional: Boolean            = sys.props.contains("functional")
  lazy val isEndToEnd: Boolean              = sys.props.contains("endtoend")
  private lazy val isSmallMessage0: Boolean = sys.props.contains("small")
  lazy val isLargeMessage: Boolean          = sys.props.contains("large")

  lazy val isRegression: Boolean =
    if (isSmoke || isFunctional || isEndToEnd) isRegression0
    else true // default

  lazy val isSmallMessage: Boolean =
    if (isLargeMessage) isSmallMessage0
    else true // default
}
