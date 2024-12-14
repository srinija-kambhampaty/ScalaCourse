import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.protobuf.functions.from_protobuf
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{Dataset, Row}
import org.apache.spark.sql.types._

object MainConsumer {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("WeeklySalesConsumer")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "/Users/srinija/gcp_key.json")
      .config("spark.sql.protobuf.enabled", "true")
      .config("spark.sql.shuffle.partitions", "50")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val kafkaBootstrapServers = "localhost:9092"
    val topic = "weekly-sales-data"
    val descriptorFile = "src/main/resources/weekly_sales.desc"
    val messageType = "data.WeeklySales"

    // Paths for saving processed data
    val trainPath = "gs://kambhampatysrinija/CaseStudy4/realTime_train.csv"
    val storeMetricsPath = "gs://kambhampatysrinija/outputs/json/store_metrics"
    val departmentMetricsPath = "gs://kambhampatysrinija/outputs/json/department_metrics"

    // Define schema explicitly to ensure consistent column types
    val schema = new StructType()
      .add("store", StringType)
      .add("dept", StringType)
      .add("date", DateType)
      .add("weekly_sales", DoubleType)
      .add("is_holiday", BooleanType)

    // Read data from Kafka
    val kafkaDF = spark.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaBootstrapServers)
      .option("subscribe", topic)
      .option("startingOffsets", "latest")
      .load()

    val rawDF = kafkaDF.selectExpr("CAST(value AS BINARY) as value")

    // Deserialize Protobuf messages
    val deserializedDF = rawDF
      .select(
        from_protobuf(col("value"), messageType, descriptorFile).alias("sales")
      )
      .select(
        col("sales.store"),
        col("sales.dept"),
        col("sales.date"),
        col("sales.weekly_sales"),
        col("sales.is_holiday")
      )
      .withColumn("date", to_date(col("date"), "yyyy-MM-dd"))

    // Define the streaming query
    val query = deserializedDF.writeStream
      .trigger(Trigger.ProcessingTime("10 seconds"))
      .foreachBatch { (batchDF: Dataset[Row], batchId: Long) =>
        println(s"Processing batch: $batchId")

        // Refresh metadata before reading historical data
        spark.catalog.refreshByPath(trainPath)

        // Read historical data with defined schema
        val historicalDF = try {
          spark.read
            .option("header", "true")
            .schema(schema) // Use explicit schema
            .csv(trainPath)
        } catch {
          case _: Exception =>
            println("Historical file not found. Initializing with an empty DataFrame.")
            spark.createDataFrame(spark.sparkContext.emptyRDD[Row], schema)
        }

        // Ensure incoming batch has consistent schema
        val processedBatchDF = batchDF
          .select(
            col("store").cast(StringType),
            col("dept").cast(StringType),
            col("date").cast(DateType),
            col("weekly_sales").cast(DoubleType),
            col("is_holiday").cast(BooleanType)
          )

        // Merge batch with historical data
        val updatedDF = historicalDF.union(processedBatchDF)
          .groupBy("store", "dept", "date", "is_holiday")
          .agg(sum("weekly_sales").alias("weekly_sales"))

        // Save updated data with append mode
        updatedDF.write.mode("append").option("header", "true").csv(trainPath)

        // Compute and save aggregated metrics
        val storeMetricsDF = updatedDF.groupBy("store")
          .agg(
            sum("weekly_sales").alias("total_weekly_sales"),
            avg("weekly_sales").alias("average_weekly_sales"),
            max("weekly_sales").alias("max_weekly_sales")
          )

        storeMetricsDF.write
          .mode("overwrite")
          .partitionBy("store")
          .json(storeMetricsPath)

        val departmentMetricsDF = updatedDF.groupBy("dept", "date")
          .agg(
            sum("weekly_sales").alias("total_sales"),
            avg("weekly_sales").alias("average_weekly_sales"),
            expr("sum(case when is_holiday = true then weekly_sales else 0 end) as holiday_sales"),
            expr("sum(case when is_holiday = false then weekly_sales else 0 end) as non_holiday_sales")
          )

        departmentMetricsDF.write
          .mode("overwrite")
          .partitionBy("dept", "date")
          .json(departmentMetricsPath)

        println(s"Batch $batchId processed successfully")
      }
      .start()

    query.awaitTermination()
  }
}
