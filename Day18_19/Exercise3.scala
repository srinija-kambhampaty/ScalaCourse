package Example3

import org.apache.spark.sql.{SparkSession, functions => F}
import org.apache.spark.sql.streaming.Trigger

object KafkaReader {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("KafkaReader")
      .master("local[*]")
      .getOrCreate()

    val kafkaBootstrapServers = "localhost:9092"
    val topic = "transactions"

    println("SparkSession created successfully.")

    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .load()

    println(s"Reading from Kafka topic: $topic at $kafkaBootstrapServers")

    val schema = new org.apache.spark.sql.types.StructType()
      .add("transactionId", org.apache.spark.sql.types.StringType)
      .add("userId", org.apache.spark.sql.types.StringType)
      .add("amount", org.apache.spark.sql.types.DoubleType)

    val transactionsDF = kafkaDF
      .selectExpr("CAST(value AS STRING) AS message", "timestamp")
      .select(
        F.from_json(F.col("message"), schema).as("data"),
        F.col("timestamp")
      )
      .select("data.transactionId", "data.amount", "timestamp")

    println("Data transformed into structured format.")

    val windowedDF = transactionsDF
      .withWatermark("timestamp", "10 seconds")
      .groupBy(
        F.window(F.col("timestamp"), "10 seconds")
      )
      .agg(F.sum("amount").as("total_amount"))

    val query = windowedDF.writeStream
      .outputMode("update")
      .trigger(Trigger.ProcessingTime("10 seconds"))
      .format("console")
      .start()

    println("Query started, awaiting termination...")

    query.awaitTermination()
    spark.stop()
    println("Spark session stopped.")

  }
}
