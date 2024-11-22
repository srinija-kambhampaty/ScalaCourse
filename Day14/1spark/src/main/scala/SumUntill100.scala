import org.apache.spark.sql.SparkSession

object SumUntill100 {
  def main(args: Array[String]): Unit = {
    // Initialize Spark session
    val spark = SparkSession.builder
      .appName("Sum of Integers")
      .master("local[1]") // Run locally with 1 thread
      .getOrCreate()

    // Create an RDD of integers from 1 to 100
    val rdd = spark.sparkContext.parallelize(1 to 100)

    // Step 1: Use reduce action to compute the sum
    val sum = rdd.reduce(_ + _)

    // Step 2: Print the result
    println(s"Sum of integers from 1 to 100: $sum")

    // Stop the Spark session
    spark.stop()
  }
}
