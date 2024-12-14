package sensor

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.protobuf.functions.from_protobuf
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.Dataset


object MainConsumer {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("IoTConsumer")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "/Users/srinija/gcp_key.json")
      .config("spark.sql.protobuf.enabled", "true")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val kafkaBootstrapServers = "localhost:9092"
    val topic = "sensor-readings"
    val descriptorFile = "src/main/resources/sensor_readings.desc"
    val messageType = "sensor.SensorReading"

    // GCS Paths
    val gcsRawPath = "gs://srinijakambhampaty/raw/sensor-data"
    val gcsAggregatedPath = "gs://srinijakambhampaty/aggregated/protobuf"
    val gcsAggregatedJsonPath = "gs://srinijakambhampaty/aggregated/json"

    // Read data from Kafka
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("subscribe", topic)
      .option("startingOffsets", "earliest")
      .load()

    val rawDF = kafkaDF.select($"value".as[Array[Byte]])

    // UDF to validate checksum
    val validateChecksum = udf { (arr: Array[Byte]) =>
      if (arr.length < 3) {
        false
      } else {
        val messageBytes = arr.dropRight(2)
        val highByte = arr(arr.length - 2)
        val lowByte = arr(arr.length - 1)
        val receivedChecksum = ((highByte & 0xFF) << 8) | (lowByte & 0xFF)
        val computedSum = messageBytes.foldLeft(0) { (acc, b) => (acc + (b & 0xFF)) & 0xFFFF }
        computedSum == receivedChecksum
      }
    }

    // UDF to remove the checksum
    val removeChecksum = udf { (arr: Array[Byte]) =>
      if (arr.length > 2) arr.take(arr.length - 2) else Array.empty[Byte]
    }

    // Validate checksum and remove invalid messages
    val validatedDF = rawDF
      .withColumn("valid", validateChecksum($"value"))
      .filter($"valid" === true)
      .withColumn("raw_proto", removeChecksum($"value"))

    // Deserialize Protobuf messages
    val deserializedDF = validatedDF
      .select(
        from_protobuf($"raw_proto", messageType, descriptorFile).alias("reading")
      )
      .select(
        $"reading.sensorId",
        $"reading.timestamp",
        $"reading.temperature",
        $"reading.humidity"
      )

    // Add partition columns
    val withPartitionCols = deserializedDF
      .withColumn("ts", to_timestamp($"timestamp" / 1000)) // Adjust if timestamp is in milliseconds
      .withColumn("year", date_format($"ts", "yyyy"))
      .withColumn("month", date_format($"ts", "MM"))
      .withColumn("day", date_format($"ts", "dd"))
      .withColumn("hour", date_format($"ts", "HH"))

    // Write validated data to GCS (raw data)
    val query = withPartitionCols.writeStream
      .outputMode("append")
      .trigger(Trigger.ProcessingTime("10 seconds"))
      .foreachBatch { (batchDF: org.apache.spark.sql.Dataset[org.apache.spark.sql.Row], batchId: Long)=>
        println(s"Processing batch: $batchId")

        // Write raw data to GCS partitioned by year, month, day, hour
        batchDF.write
          .mode("append")
          .partitionBy("year", "month", "day", "hour")
          .parquet(gcsRawPath)

        // Perform aggregation by reading existing aggregated data
        val partitionPath = s"$gcsAggregatedPath/year=${batchDF.select(max("year")).first().getString(0)}/month=${batchDF.select(max("month")).first().getString(0)}/day=${batchDF.select(max("day")).first().getString(0)}/hour=${batchDF.select(max("hour")).first().getString(0)}"
        val jsonPartitionPath = s"$gcsAggregatedJsonPath/year=${batchDF.select(max("year")).first().getString(0)}/month=${batchDF.select(max("month")).first().getString(0)}/day=${batchDF.select(max("day")).first().getString(0)}/hour=${batchDF.select(max("hour")).first().getString(0)}"

        // Refresh metadata for Parquet and JSON paths
        spark.catalog.refreshByPath(partitionPath)
        spark.catalog.refreshByPath(jsonPartitionPath)

        val currentAggregatedDF = batchDF
          .groupBy("sensorId")
          .agg(
            avg("temperature").alias("avg_temperature"),
            max("temperature").alias("max_temperature"),
            min("temperature").alias("min_temperature"),
            avg("humidity").alias("avg_humidity"),
            max("humidity").alias("max_humidity"),
            min("humidity").alias("min_humidity")
          )

        val existingAggregatedDF = try {
          spark.read.parquet(partitionPath)
        } catch {
          case _: Exception =>
            println(s"No existing aggregated data found at $partitionPath. Initializing with empty DataFrame.")
            spark.createDataFrame(spark.sparkContext.emptyRDD[Row], currentAggregatedDF.schema)
        }

        val combinedAggregatedDF = existingAggregatedDF.union(currentAggregatedDF)
          .groupBy("sensorId")
          .agg(
            avg("avg_temperature").alias("avg_temperature"),
            greatest(max("max_temperature"), max("max_temperature")).alias("max_temperature"),
            least(min("min_temperature"), min("min_temperature")).alias("min_temperature"),
            avg("avg_humidity").alias("avg_humidity"),
            greatest(max("max_humidity"), max("max_humidity")).alias("max_humidity"),
            least(min("min_humidity"), min("min_humidity")).alias("min_humidity")
          )

        try {
          combinedAggregatedDF.write
            .mode("overwrite")
            .parquet(partitionPath)
          println(s"Parquet data written successfully to $partitionPath")
        } catch {
          case e: Exception =>
            println(s"Error writing Parquet data to $partitionPath: ${e.getMessage}")
        }


        // Write updated aggregated data to GCS
        combinedAggregatedDF.write
          .mode("overwrite")
          .parquet(partitionPath)


        // Handle existing aggregated data (JSON)
        val existingJsonAggregatedDF = try {
          spark.read.json(jsonPartitionPath)
        } catch {
          case _: Exception =>
            println(s"File not found at $jsonPartitionPath. Initializing with empty DataFrame.")
            spark.createDataFrame(spark.sparkContext.emptyRDD[Row], currentAggregatedDF.schema)
        }

        val finalJsonAggregatedDF = existingJsonAggregatedDF.union(combinedAggregatedDF)
          .groupBy("sensorId")
          .agg(
            avg("avg_temperature").alias("avg_temperature"),
            greatest(max("max_temperature"), max("max_temperature")).alias("max_temperature"),
            least(min("min_temperature"), min("min_temperature")).alias("min_temperature"),
            avg("avg_humidity").alias("avg_humidity"),
            greatest(max("max_humidity"), max("max_humidity")).alias("max_humidity"),
            least(min("min_humidity"), min("min_humidity")).alias("min_humidity")
          )

        try {
          finalJsonAggregatedDF.write
            .mode("overwrite")
            .json(jsonPartitionPath)
          println(s"JSON data written successfully to $jsonPartitionPath")
        } catch {
          case e: Exception =>
            println(s"Error writing JSON data to $jsonPartitionPath: ${e.getMessage}")
        }
        // Write updated aggregated data to GCS (JSON)
        finalJsonAggregatedDF.write
          .mode("overwrite")
          .json(jsonPartitionPath)


        println(s"Batch $batchId processed and aggregated successfully")
      }
      .option("checkpointLocation", "gs://srinijakambhampaty/checkpoints/sensor-data")
      .start()

    query.awaitTermination()
  }
}
