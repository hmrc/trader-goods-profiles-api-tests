#!/usr/bin/env bash

ENVIRONMENT=$1

sbt clean -Denvironment="${ENVIRONMENT:=local}" "testOnly uk.gov.hmrc.tgp.tests.specs.*"
