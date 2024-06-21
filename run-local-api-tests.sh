#!/usr/bin/env bash

ENVIRONMENT=$1
sbt scalafmtCheckAll scalafmtCheck
sbt scalafmtSbt
sbt scalafmtAll

sm2 --update-config
sbt clean -Denvironment="${ENVIRONMENT:=local}" "testOnly uk.gov.hmrc.tgp.tests.specs.*"
