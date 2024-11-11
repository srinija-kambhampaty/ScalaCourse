
import JsonFormats._
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.typed.Http
import akka.http.scaladsl.typed.scaladsl.RouteAdapter
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import org.apache.kafka.clients.producer.ProducerRecord
import spray.json._
import java.util.Properties

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await


object Exec {
  def main(args: Array[String]): Unit = {
    val message = "Hello You!"

    val system = ActorSystem("MessageSystem")
    val networkLeaner = system.actorOf(Props[NetworkMessages], "NetworkMessages")
    val cloudLearner = system.actorOf(Props[CloudMessages], "CloudMessages")
    val appLearner = system.actorOf(Props[AppMessages], "AppMessages")
    val handlerLearner = system.actorOf(Props(new MessageHandler(networkLeaner, cloudLearner, appLearner)), "MessageHandler")

    val route = post {
      path("write-message") {
        entity(as[Message]) { message =>
          val jsonString = message.toJson.toString()
//          val record = new ProducerRecord[String,String]()
          handlerLearner ! NetworkMsg(jsonString)
          println(s"Sent to nesasge handler")
          complete(StatusCodes.OK, "Message processed")

        }
      }

    }

    Http().newServerAt("localhost", 8080).bind(route)
    println("Server online at http://localhost:8080/")

    handlerLearner ! NetworkMsg(message)
    handlerLearner ! CloudMsg(message)
    handlerLearner ! AppMsg(message)

    implicit val timeout: Timeout = Timeout(5.seconds)
    val future = handlerLearner ? messageListner(NetworkMessage)
    val result = Await.result(future, timeout.duration)

    println(s"Retrieved message from handler: $result")
    system.terminate()
  }
}

