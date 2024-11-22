import org.apache.spark.sql.SparkSession

object SumUntill100 {
  def main(args: Array[String]): Unit = {
    // Initialize Spark session
    val spark = SparkSession.builder
      .appName("Sum of Integers")
      .master("local[1]")
      .getOrCreate()

    val rdd = spark.sparkContext.parallelize(1 to 100)
    val sum = rdd.reduce(_ + _)
    println(s"Sum of integers from 1 to 100: $sum")
    spark.stop()
  }
}
