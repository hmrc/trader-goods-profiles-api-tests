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
