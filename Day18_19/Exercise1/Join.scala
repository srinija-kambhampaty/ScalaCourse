package Exercise1

import org.apache.spark.sql.SparkSession

object Join {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Join User and Transaction Data")
      .master("local[*]")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "/Users/srinija/gcp-key.json")
      .getOrCreate()

    // Define GCS paths
    val userFilePath = "gs://srinija/user_data.csv"
    val transactionFilePath = "gs://srinija/transaction_data.csv"

    // Load data from GCS
    val userDF = spark.read.option("header", "true").csv(userFilePath)
    val transactionDF = spark.read.option("header", "true").csv(transactionFilePath)

    // Perform join operation on user_id
    val joinedData = userDF.join(transactionDF, "user_id")

    // Output joined data path
    val joinedOutputPath = "gs://srinija/joined_data.csv"
    joinedData.write
      .option("header", "true")
      .csv(joinedOutputPath)

    println(s"Joined data successfully written to: $joinedOutputPath")
    spark.stop()
  }
}
