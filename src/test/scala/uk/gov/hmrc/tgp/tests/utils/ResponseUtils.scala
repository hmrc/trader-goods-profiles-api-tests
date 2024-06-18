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

package uk.gov.hmrc.tgp.tests.utils

import play.api.libs.json.{JsObject, Json}

object ResponseUtils {

  def validateToReviewAndReviewReason(records: List[JsObject]): Boolean =
    records.exists { record =>
      val adviceStatus = (record \ "adviceStatus").as[String]
      val toReview     = adviceStatus match {
        case "Requested" | "In progress" | "Information requested" => true
        case _                                                     => false
      }
      val reviewReason = adviceStatus match {
        case "Requested"                             =>
          "The commodity code has expired. You'll need to change the commodity code and categorise the goods."
        case "In progress" | "Information requested" =>
          "The restrictions have changed or there may be new restrictions. You need to categorise the record."
        case _                                       => ""
      }

      toReview == (record \ "toReview").as[Boolean] &&
      reviewReason == (record \ "reviewReason").asOpt[String].getOrElse("")
    }

  def compareJson(actualJson: String, expectedJson: String): Boolean =
    Json.toJson(Json.parse(actualJson)) == Json.parse(expectedJson)

  def validateAdviceAndLockedStatus(
    records: List[JsObject],
    expectedAdviceStatus: String,
    expectedLocked: Boolean
  ): Boolean =
    records.exists { record =>
      val actualAdviceStatus = (record \ "adviceStatus").as[String]
      val locked             = (record \ "locked").as[Boolean]

      actualAdviceStatus.equalsIgnoreCase(expectedAdviceStatus) && locked == expectedLocked
    }

}
