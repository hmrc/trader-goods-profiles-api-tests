package uk.gov.hmrc.tgp.tests.environments.models

import play.api.libs.json.{Format, Json}

object BoxId {
  implicit val format: Format[BoxId] = Json.valueFormat[BoxId]
}

case class BoxId(value: String) extends AnyVal
