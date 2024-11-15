package kafka

import akka.actor.Actor
import models.AllocationRequest
import play.api.libs.json.Json
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, ConsumerRecords, ConsumerRecord}
import java.util.Properties
import org.slf4j.LoggerFactory

class OverdueReminderHandler extends Actor {
  private val logger = LoggerFactory.getLogger("OverdueReminderLogger")

  // Kafka Consumer configuration
  private val consumerConfig = new Properties()
  consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "overdue-reminder-group")
  consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")

  // Create Kafka consumer and subscribe to the "overdue_reminders" topic
  val kafkaConsumer = new KafkaConsumer[String, String](consumerConfig)
  kafkaConsumer.subscribe(java.util.Collections.singletonList("overdue_reminders"))

  override def receive: Receive = {
    case "start-consumer" => consumeMessages()

    case message: String =>
      // Parse the JSON message to the AllocationRequest model
      Json.parse(message).validate[AllocationRequest].asOpt match {
        case Some(allocationRequest) =>
          println(s"Overdue reminder for employee: ${allocationRequest.userId}, equipment ID: ${allocationRequest.equipmentId}")
          logger.info(s"Overdue Reminder: User ID ${allocationRequest.userId} - Equipment ID ${allocationRequest.equipmentId}")
          logger.info(s"Raw Data: $allocationRequest")
        case None =>
          logger.error(s"Failed to parse message: $message")
      }
  }

  def consumeMessages(): Unit = {
    new Thread(() => {
      while (true) {
        val records: ConsumerRecords[String, String] = kafkaConsumer.poll(1000)
        records.forEach { record: ConsumerRecord[String, String] =>
          val allocationRequestJson = record.value()
          self ! allocationRequestJson
        }
      }
    }).start()
  }
}
