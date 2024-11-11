import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

sealed trait MessageType
case object NetworkMessage extends MessageType
case object CloudMessage extends MessageType
case object AppMessage extends MessageType

case class NetworkMsg(message: String)
case class CloudMsg(message: String)
case class AppMsg(message: String)
case class processMessage(message: String)
case class messageListner(messageType: MessageType)

class MessageHandler(networkMessager: ActorRef, cloudMessager: ActorRef, appMessager: ActorRef) extends Actor {
  private var messages: Option[String] = None

  def receive: Receive = {
    case NetworkMsg(message) =>
      println(s"Received NetworkMsg: $message")
      networkMessager ! processMessage(message)

    case CloudMsg(message) =>
      println(s"Received CloudMsg: $message")
      cloudMessager ! processMessage(message)

    case AppMsg(message) =>
      println(s"Received AppMsg: $message")
      appMessager ! processMessage(message)

    case messageListner(messageType) =>
      println(s"Received messageListener for $messageType")
      messageType match {
        case NetworkMessage =>
          sender() ! s"Network message: ${messages.getOrElse("No message available")}"
        case CloudMessage =>
          sender() ! s"Cloud message: ${messages.getOrElse("No message available")}"
        case AppMessage =>
          sender() ! s"App message: ${messages.getOrElse("No message available")}"
      }
  }
}

class NetworkMessages extends Actor {
  def receive: Receive = {
    case processMessage(message) =>
      println(s"Network processing: $message")
      sender() ! messageListner(NetworkMessage)
  }
}

class CloudMessages extends Actor {
  def receive: Receive = {
    case processMessage(message) =>
      println(s"Cloud processing: $message")
      sender() ! messageListner(CloudMessage)
  }
}

class AppMessages extends Actor {
  def receive: Receive = {
    case processMessage(message) =>
      println(s"App processing: $message")
      sender() ! messageListner(AppMessage)
  }
}