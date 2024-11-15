import akka.actor.{ActorSystem, Props}
import kafka.{MaintenanceAlertHandler,InventoryAlertHandler,OverdueReminderHandler}

object Main extends App {
  // Initialize the Akka system
  val system = ActorSystem("MaintenanceAlertSystem")

  // Create the MaintenanceAlertHandler actor
  val maintenanceAlertHandler = system.actorOf(Props(new MaintenanceAlertHandler()), "maintenanceAlertHandler")

  // Create the InventoryAlertHandler actor
  val inventoryAlertHandler = system.actorOf(Props(new InventoryAlertHandler()), "inventoryAlertHandler")

  // Create the OverdueReminderHandler actor
  val overdueReminderHandler = system.actorOf(Props(new OverdueReminderHandler()), "overdueReminderHandler")

  // Start the Kafka consumer
  maintenanceAlertHandler ! "start-consumer"
  // Start the Kafka consumer
  inventoryAlertHandler ! "start-consumer"
  // Start the Kafka consumer
  overdueReminderHandler ! "start-consumer"

}
