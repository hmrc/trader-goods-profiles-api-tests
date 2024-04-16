package uk.gov.hmrc.tgp.tests.common

import io.restassured.RestAssured.`given`
import io.restassured.http.Cookies
import io.restassured.path.json.JsonPath
import io.restassured.response.Response

import java.io.InputStream
import scala.io.Source
import scala.util.matching.Regex

trait EnvironmentHelpers {}
