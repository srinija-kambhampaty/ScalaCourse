package Exercise1

import org.apache.spark.sql.SparkSession

object DataGCS {
  def main(args: Array[String]): Unit = {
    // Configure and initialize SparkSession
    val spark = SparkSession.builder()
      .appName("Data Upload to Google Cloud Storage")
      .master("local[*]")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "/Users/srinija/gcp-key.json")
      .getOrCreate()

    // Create synthetic user data (100,000 records)
    val users = (1 to 100000).map(id => (id, s"User_$id"))
    val userDF = spark.createDataFrame(users).toDF("user_id", "name")

    // Generate synthetic transaction data (1,000 records)
    val rng = new scala.util.Random
    val transactions = (1 to 1000).map { _ =>
      val userId = rng.nextInt(100000) + 1
      val transType = if (rng.nextBoolean()) "Purchase" else "Refund"
      val amount = 1.0 + rng.nextDouble() * (1000.0 - 1.0) // Generate random amount
      (userId, transType, amount)
    }
    val transactionDF = spark.createDataFrame(transactions).toDF("user_id", "transaction_type", "transaction_amount")

    // Define paths for Google Cloud Storage
    val userFilePath = "gs://srinija/user_data.csv"
    val transactionFilePath = "gs://srinija/transaction_data.csv"

    // Save DataFrames to GCS in CSV format
    userDF.write
      .option("header", "true")
      .csv(userFilePath)

    transactionDF.write
      .option("header", "true")
      .csv(transactionFilePath)

    println(s"Data uploaded to GCS: \nUser Data: $userFilePath \nTransaction Data: $transactionFilePath")
    spark.stop()
  }
}
