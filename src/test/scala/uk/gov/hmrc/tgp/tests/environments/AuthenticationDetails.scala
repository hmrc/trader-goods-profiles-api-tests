package uk.gov.hmrc.tgp.tests.environments

import uk.gov.hmrc.tgp.tests.environments.models.BoxId
import uk.gov.hmrc.tgp.tests.environments.models.ClientId

case class AuthenticationResult(value: Either[AuthenticationFailure, AuthenticationDetails], eori: String) {
  lazy val isValid: Boolean = value.isRight

  lazy val getOrThrow: AuthenticationDetails =
    if (isValid) value.right.get
    else throw value.left.get.exception
}

case class AuthenticationDetails(userBearerToken: String, clientId: Option[ClientId] = None, boxId: Option[BoxId] = None)

case class AuthenticationFailure(message: String, cause: Option[Throwable]) {

  lazy val exception: Exception =
    cause.map(new IllegalStateException(message, _)).getOrElse(new IllegalStateException(message))
}
