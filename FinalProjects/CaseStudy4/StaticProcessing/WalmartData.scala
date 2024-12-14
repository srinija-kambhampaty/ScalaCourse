import org.apache.spark.sql.{DataFrame, SparkSession, SaveMode}
import org.apache.spark.sql.functions._

object WalmartData {

  def main(args: Array[String]): Unit = {
    // Initialize SparkSession
    val spark: SparkSession = SparkSession.builder()
      .appName("SensorDataAggregation")
      .config("spark.hadoop.fs.defaultFS", "gs://kambhampatysrinija/")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "/Users/srinija/gcp_key.json")
      .config("spark.sql.shuffle.partitions", "20") // Optimize shuffle operations
      .config("spark.executor.memory", "8g")
      .config("spark.executor.cores", "4")
      .master("local[*]")
      .getOrCreate()

    // Set the GCS paths for the datasets
    val trainPath = "gs://kambhampatysrinija/CaseStudy4/train.csv"
    val featuresPath = "gs://kambhampatysrinija/CaseStudy4/features.csv"
    val storesPath = "gs://kambhampatysrinija/CaseStudy4/stores.csv"

    // Paths for storing the results
    val parquetPath = "gs://kambhampatysrinija/outputs/parquet"
    val jsonPath = "gs://kambhampatysrinija/outputs/json"

    // Load the datasets
    val trainDF = spark.read.option("header", "true").option("inferSchema", "true").csv(trainPath).persist()
    val featuresDF = spark.read.option("header", "true").option("inferSchema", "true").csv(featuresPath).persist()
    val storesDF = spark.read.option("header", "true").option("inferSchema", "true").csv(storesPath).persist()

    // Validate the data
    val validatedTrainDF = validateTrainData(trainDF)
    val cleanedFeaturesDF = cleanFeaturesData(featuresDF)
    val cleanedStoresDF = cleanStoresData(storesDF)

    // Rename IsHoliday in featuresDF to avoid ambiguity
    val renamedFeaturesDF = cleanedFeaturesDF.withColumnRenamed("IsHoliday", "IsHolidayFeature").persist()

    // Cache the cleaned stores data
    cleanedStoresDF.persist()

    // Enrich the train data with features and stores metadata
    val enrichedDF = enrichData(validatedTrainDF, renamedFeaturesDF, cleanedStoresDF).persist()

    // Compute Aggregation Metrics
    val storeMetricsDF = computeStoreMetrics(enrichedDF).persist()
    val departmentMetricsDF = computeDepartmentMetrics(enrichedDF).persist()

    // Partitioned Storage in Parquet Format
    val partitionedEnrichedDF = enrichedDF
      .repartition(col("Store"), col("Date")) // Partition by Store and Date

    partitionedEnrichedDF.limit(1000).write
      .mode(SaveMode.Overwrite)
      .partitionBy("Store", "Date") // Physically partition the data
      .parquet(parquetPath)

    // Store aggregated metrics in JSON format with partitioning
    storeMetricsDF.write
      .mode(SaveMode.Overwrite)
      .partitionBy("Store") // Partition by Store
      .json(s"$jsonPath/store_metrics")

    departmentMetricsDF.limit(20).write
      .mode(SaveMode.Overwrite)
      .partitionBy("Dept", "Date") // Partition by Department and Date
      .json(s"$jsonPath/department_metrics")

    // Show Aggregated Metrics
    println("Store-Level Metrics:")
    storeMetricsDF.show(false)

    println("Department-Level Metrics:")
    departmentMetricsDF.show(false)

    // Stop SparkSession
    spark.stop()
  }

  def validateTrainData(trainDF: DataFrame): DataFrame = {
    // Filter out records with negative Weekly_Sales and ensure no missing critical values
    trainDF.filter(col("Weekly_Sales") >= 0)
      .na.drop(Seq("Store", "Dept", "Date", "Weekly_Sales"))
  }

  def cleanFeaturesData(featuresDF: DataFrame): DataFrame = {
    // Drop rows with missing critical values
    featuresDF.na.drop(Seq("Store", "Date"))
  }

  def cleanStoresData(storesDF: DataFrame): DataFrame = {
    // Drop rows with missing critical values
    storesDF.na.drop(Seq("Store", "Type", "Size"))
  }

  def enrichData(trainDF: DataFrame, featuresDF: DataFrame, storesDF: DataFrame): DataFrame = {
    // Perform join operations with features and stores datasets
    val trainWithFeaturesDF = trainDF.join(featuresDF, Seq("Store", "Date"), "left")
    trainWithFeaturesDF.join(storesDF, Seq("Store"), "left")
  }

  def computeStoreMetrics(enrichedDF: DataFrame): DataFrame = {
    enrichedDF.groupBy("Store")
      .agg(
        sum("Weekly_Sales").alias("Total_Weekly_Sales"),
        avg("Weekly_Sales").alias("Average_Weekly_Sales"),
        max("Weekly_Sales").alias("Top_Performance_Weekly_Sales")
      )
  }

  def computeDepartmentMetrics(enrichedDF: DataFrame): DataFrame = {
    enrichedDF.groupBy("Dept", "Date")
      .agg(
        sum("Weekly_Sales").alias("Total_Sales"),
        avg("Weekly_Sales").alias("Average_Weekly_Sales"),
        expr("sum(case when IsHolidayFeature = true then Weekly_Sales else 0 end) as Holiday_Sales"),
        expr("sum(case when IsHolidayFeature = false then Weekly_Sales else 0 end) as NonHoliday_Sales")
      )
  }
}
