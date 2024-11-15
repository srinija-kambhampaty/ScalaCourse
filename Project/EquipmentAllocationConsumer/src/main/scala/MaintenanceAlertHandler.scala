package kafka

import akka.actor.Actor
import models.Equipment
import play.api.libs.json.Json
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer, ConsumerRecords, ConsumerRecord}
import java.util.Properties
import org.slf4j.LoggerFactory

class MaintenanceAlertHandler extends Actor {
  private val logger = LoggerFactory.getLogger("MaintenanceAlertLogger")

  // Kafka Consumer configuration
  private val consumerConfig = new Properties()
  consumerConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  consumerConfig.put(ConsumerConfig.GROUP_ID_CONFIG, "maintenance-alert-group")
  consumerConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  consumerConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")

  // Create Kafka consumer and subscribe to the "maintenance_alerts" topic
  val kafkaConsumer = new KafkaConsumer[String, String](consumerConfig)
  kafkaConsumer.subscribe(java.util.Collections.singletonList("maintenance_alerts"))

  override def receive: Receive = {
    case "start-consumer" => consumeMessages()

    case message: String =>
      // Parse the JSON message to the Equipment model
      Json.parse(message).validate[Equipment].asOpt match {
        case Some(equipment) =>
          println(s"Received maintenance alert for equipment: $equipment")
          logger.info(s"Maintenance Alert: Equipment ID ${equipment.id} - Condition ${equipment.conditionStatus}")
          logger.info(s"Raw Data: $equipment")
        case None =>
          logger.error(s"Failed to parse message: $message")
      }
  }

  def consumeMessages(): Unit = {
    new Thread(() => {
      while (true) {
        val records: ConsumerRecords[String, String] = kafkaConsumer.poll(1000)
        records.forEach { record: ConsumerRecord[String, String] =>
          val equipmentJson = record.value()
          self ! equipmentJson
        }
      }
    }).start()
  }
}
