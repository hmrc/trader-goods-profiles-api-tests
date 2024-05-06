

package uk.gov.hmrc.tgp.tests.client

import akka.actor.ActorSystem
import play.api.libs.ws.DefaultBodyWritables._
import play.api.libs.ws.StandaloneWSRequest
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.{ExecutionContext, Future}

trait HttpClient {

  implicit val actorSystem: ActorSystem = ActorSystem()
  val wsClient: StandaloneAhcWSClient   = StandaloneAhcWSClient()
  implicit val ec: ExecutionContext     = ExecutionContext.global

  def get(url: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
      .get()

  def post(url: String, bodyAsJson: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
      .post(bodyAsJson)

  def delete(url: String, headers: (String, String)*): Future[StandaloneWSRequest#Self#Response] =
    wsClient
      .url(url)
      .withHttpHeaders(headers: _*)
      .delete()

}
