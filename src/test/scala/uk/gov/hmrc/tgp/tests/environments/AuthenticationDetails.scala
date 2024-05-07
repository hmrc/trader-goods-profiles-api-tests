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

import uk.gov.hmrc.tgp.tests.environments.models.BoxId
import uk.gov.hmrc.tgp.tests.environments.models.ClientId

case class AuthenticationResult(value: Either[AuthenticationFailure, AuthenticationDetails], eori: String) {
  lazy val isValid: Boolean = value.isRight

  lazy val getOrThrow: AuthenticationDetails =
    if (isValid) value.right.get
    else throw value.left.get.exception
}

case class AuthenticationDetails(
  userBearerToken: String,
  clientId: Option[ClientId] = None,
  boxId: Option[BoxId] = None
)

case class AuthenticationFailure(message: String, cause: Option[Throwable]) {

  lazy val exception: Exception =
    cause.map(new IllegalStateException(message, _)).getOrElse(new IllegalStateException(message))
}
