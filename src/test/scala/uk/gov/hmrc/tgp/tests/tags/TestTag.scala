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

package uk.gov.hmrc.tgp.tests.tags

import org.scalatest.Ignore
import org.scalatest.Tag
import uk.gov.hmrc.tgp.tests.TestEnvironment

import scala.language.implicitConversions

object TestTag {

  implicit def toTag(value: TestTag): Tag = value.asTag

  implicit class RichTestTags(val value: TestTag) extends AnyVal {
    def and(other: TestTag): TestTag = TestTagImpl(value.value && other.value)
    def or(other: TestTag): TestTag  = TestTagImpl(value.value || other.value)
    def not: TestTag                 = TestTagImpl(!value.value)

    def asTag: Tag =
      if (value.value) Tag("notignored")
      else Tag(classOf[Ignore].getName)
  }

  // Various tags that might be turned into ignored tests

  val LOCAL_ONLY: TestTag  = TestTagImpl(TestEnvironment.isLocal)
  val REMOTE_ONLY: TestTag = TestTagImpl(!TestEnvironment.isLocal)

}

sealed trait TestTag extends Any {
  def value: Boolean
}
private case class TestTagImpl private (value: Boolean) extends AnyVal with TestTag
