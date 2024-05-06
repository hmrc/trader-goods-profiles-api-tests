

package uk.gov.hmrc.tgp.tests

import org.scalatest._
import uk.gov.hmrc.tgp.tests.environments.AuthenticationResult
import org.scalatest.matchers.must.Matchers

sealed trait TestSuite extends SuiteMixin with Matchers { self: Suite =>

  def shouldExecuteTestType: Boolean    = false
  def shouldExecuteEnvironment: Boolean = true

  lazy val shouldExecute: Boolean = shouldExecuteTestType && shouldExecuteEnvironment

  /** Override this if you need a specific auth token for a test (e.g. a different EORI).
    */
  lazy val authToken: AuthenticationResult = TestEnvironment.authenticationDetails

  abstract override def run(testName: Option[String], args: Args): Status =
    if (args.stopper.stopRequested) FailedStatus
    else if (shouldExecute) super.run(testName, args)
    else SucceededStatus

  abstract override def expectedTestCount(filter: Filter): Int =
    if (shouldExecute) super.expectedTestCount(filter)
    else 0

  abstract override protected def runTest(testName: String, args: Args): Status =
    if (args.stopper.stopRequested) FailedStatus
    else if (authToken.isValid) super.runTest(testName, args)
    else {
      args.stopper.requestStop() // we want the test suite to abort all runs
      throw authToken.value.left.get.exception // forces the suite to be marked as aborted
    }
}

trait SmokeTestSuite extends TestSuite { self: Suite =>

  override def shouldExecuteTestType: Boolean =
    super.shouldExecuteTestType || TestEnvironment.isSmoke

}

trait RegressionTestSuite extends TestSuite { self: Suite =>

  override def shouldExecuteTestType: Boolean =
    super.shouldExecuteTestType || TestEnvironment.isRegression

}

trait EndToEndTestSuite extends TestSuite { self: Suite =>

  override def shouldExecuteTestType: Boolean =
    super.shouldExecuteTestType || TestEnvironment.isEndToEnd

}

trait FunctionalTestSuite extends TestSuite { self: Suite =>

  override def shouldExecuteTestType: Boolean =
    super.shouldExecuteTestType || TestEnvironment.isFunctional

}

trait LargeMessageOnly extends TestSuite { self: Suite =>

  override def shouldExecuteTestType: Boolean =
    super.shouldExecuteTestType && TestEnvironment.isLargeMessage

}

trait SmallMessageOnly extends TestSuite { self: Suite =>

  override def shouldExecuteTestType: Boolean =
    super.shouldExecuteTestType && TestEnvironment.isSmallMessage

}

trait LocalOnly extends TestSuite { self: Suite =>

  override def shouldExecuteEnvironment: Boolean = TestEnvironment.isLocal

}

trait RemoteOnly extends TestSuite { self: Suite =>

  override def shouldExecuteEnvironment: Boolean = !TestEnvironment.isLocal

}

trait RequiresTestSupport extends TestSuite { self: Suite =>

  override def shouldExecuteEnvironment: Boolean =
    super.shouldExecuteEnvironment && TestEnvironment.environment.hasTestSupport

}

trait RequiresObjectStore extends TestSuite { self: Suite =>

  override def shouldExecuteEnvironment: Boolean =
    super.shouldExecuteEnvironment && TestEnvironment.environment.supportsObjectStore

}
