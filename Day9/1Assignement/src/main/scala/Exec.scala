import JsonFormats._
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
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
import KafkaProducer._  // Import the companion object to access createProducer

object Exec {
  def main(args: Array[String]): Unit = {
    val message = "Hello You!"

    implicit val system = ActorSystem("MessageSystem")
    val networkLeaner = system.actorOf(Props[NetworkMessages], "NetworkMessages")
    val cloudLearner = system.actorOf(Props[CloudMessages], "CloudMessages")
    val appLearner = system.actorOf(Props[AppMessages], "AppMessages")
    val handlerLearner = system.actorOf(Props(new MessageHandler(networkLeaner, cloudLearner, appLearner)), "MessageHandler")

    // Create KafkaProducer instance by calling the method from the KafkaProducer companion object
    val producer = createProducer()  // Use createProducer from the companion object

    val route = post {
      path("write-message") {
        entity(as[Message]) { message =>
          val jsonString = message.toJson.toString()
          handlerLearner ! NetworkMsg(jsonString)
          handlerLearner ! CloudMsg(jsonString)
          handlerLearner ! AppMsg(jsonString)

          implicit val timeout: Timeout = Timeout(5.seconds)
          val future = handlerLearner ? messageListner(NetworkMessage)
          val result = Await.result(future, timeout.duration)

          println(s"Retrieved message from handler: $result")
          system.terminate()

          // Send the message to Kafka using the producer
          val record = new ProducerRecord[String, String]("message-topic", jsonString)
          producer.send(record)  // Send the record to Kafka
          println(s"Sent to message handler")

          complete(StatusCodes.OK, "Message processed")
        }
      }
    }

    Http().newServerAt("localhost", 8080).bind(route)
    println("Server online at http://localhost:8080/")
  }
}
