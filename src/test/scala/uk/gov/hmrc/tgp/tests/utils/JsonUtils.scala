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

import scala.io.Source

object JsonUtils {

  def getResponseJsonFileAsString(folderName: String, fileName: String): String =
    Source.fromResource(s"Json.Response/All/$folderName/$fileName.json").mkString

  def getRequestJsonFileAsString(folderName: String, fileName: String): String =
    Source.fromResource(s"Json.request/All/$folderName/$fileName.json").mkString
}
