package uk.gov.hmrc.tgp.tests

import uk.gov.hmrc.tgp.tests.environments.Development
import uk.gov.hmrc.tgp.tests.environments.Environment
import uk.gov.hmrc.tgp.tests.environments.Local

object TestEnvironment {

  lazy val environment: Environment =
    sys.props.get("environment").map(_.toLowerCase).map(Environment.environments).getOrElse(Local)

  lazy val isLocal: Boolean       = environment == Local
  lazy val isDevelopment: Boolean = environment == Development

}
