import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import akka.stream.Materializer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import scala.concurrent.duration._
import scala.util.Random
import data.Weekly_sales.WeeklySales
import scalapb.GeneratedMessage

object MainProducer extends App {
  implicit val system: ActorSystem = ActorSystem("WeeklySalesProducer")
  implicit val mat: Materializer = Materializer(system)
  import system.dispatcher

  // Kafka configuration
  val bootstrapServers = "localhost:9092"
  val topic = "weekly-sales-data"

  // Producer settings with String key and Byte array value
  val producerSettings = ProducerSettings(system, new StringSerializer, new ByteArraySerializer)
    .withBootstrapServers(bootstrapServers)

  // Function to generate random WeeklySales data
  def generateRandomWeeklySales(): WeeklySales = {
    val storeId = Random.nextInt(10) + 1 // Store IDs from 1 to 10
    val deptId = Random.nextInt(20) + 1 // Department IDs from 1 to 20
    val date = java.time.LocalDate.now().toString // Current date as YYYY-MM-DD
    val weeklySales = Random.nextFloat() * 10000 // Random weekly sales between 0 and 10,000
    val isHoliday = Random.nextBoolean() // Random boolean for holiday status

    WeeklySales(
      store = storeId.toString,
      dept = deptId.toString,
      date = date,
      weeklySales = weeklySales,
      isHoliday = isHoliday
    )
  }

  // Serialize Protobuf message
  def serializeProtobuf[T <: GeneratedMessage](message: T): Array[Byte] = {
    message.toByteArray
  }

  // Create a source that emits a record every 5 seconds
  val salesSource = Source.tick(0.seconds, 5.seconds, ())
    .map { _ =>
      val weeklySales = generateRandomWeeklySales()
      val serializedData = serializeProtobuf(weeklySales)
      new ProducerRecord[String, Array[Byte]](topic, weeklySales.store, serializedData)
    }

  // Run the stream to produce records to Kafka
  salesSource
    .runWith(Producer.plainSink(producerSettings))
    .onComplete { result =>
      println(s"Stream completed with result: $result")
      system.terminate()
    }

  scala.concurrent.Await.result(system.whenTerminated, scala.concurrent.duration.Duration.Inf)
}
