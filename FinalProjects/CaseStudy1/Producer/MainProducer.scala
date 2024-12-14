import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import akka.stream.Materializer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}

import sensor.sensor_readings.SensorReading
import scalapb.GeneratedMessage
import scala.concurrent.duration._
import scala.util.Random

object MainProducer extends App {
  implicit val system: ActorSystem = ActorSystem("SensorDataProducer")
  implicit val mat: Materializer = Materializer(system)
  import system.dispatcher

  // Kafka configuration
  val bootstrapServers = "localhost:9092"
  val topic = "sensor-readings"

  // Producer settings with String key and Byte array value
  val producerSettings = ProducerSettings(system, new StringSerializer, new ByteArraySerializer)
    .withBootstrapServers(bootstrapServers)

  // Function to generate a random SensorReading
  def generateRandomReading(sensorId: String): SensorReading = {
    val timestamp = System.currentTimeMillis()
    val temperature = -50 + Random.nextFloat() * 200f // random float between -50 and 150
    val humidity = Random.nextFloat() * 100f // random float between 0 and 100

    SensorReading(
      sensorId = sensorId,
      timestamp = timestamp,
      temperature = temperature,
      humidity = humidity
    )
  }

  // Serialize Protobuf
  def serializeProtobuf[T <: GeneratedMessage](message: T): Array[Byte] = {
    message.toByteArray
  }

  def computeChecksum(bytes: Array[Byte]): Array[Byte] = {
    val sum = bytes.foldLeft(0) { (acc, b) => (acc + (b & 0xFF)) & 0xFFFF }
    val highByte = (sum >> 8).toByte
    val lowByte = (sum & 0xFF).toByte
    Array(highByte, lowByte)
  }

  val sensorId = "sensor-1"

  // Create a source that emits a reading every 100 milliseconds
  val sensorSource = Source.tick(0.millis, 100.millis, ())
    .map { _ =>
      val reading = generateRandomReading(sensorId)
      val messageBytes = serializeProtobuf(reading)
      val checksumBytes = computeChecksum(messageBytes)

      // Final payload: protobuf message + 2-byte checksum
      val finalPayload = messageBytes ++ checksumBytes

      new ProducerRecord[String, Array[Byte]](topic, reading.sensorId, finalPayload)
    }

  // Run the stream to produce records to Kafka
  sensorSource
    .runWith(Producer.plainSink(producerSettings))
    .onComplete { result =>
      println(s"Stream completed with result: $result")
      system.terminate()
    }

  scala.concurrent.Await.result(system.whenTerminated, scala.concurrent.duration.Duration.Inf)
}
