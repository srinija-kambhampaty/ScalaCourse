import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object NoOfWords {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("NoOfWords")
      .master("local[1]")
      .getOrCreate()

    val data = Seq(
      "Spark is fast and general-purpose",
      "It provides powerful data processing",
      "RDDs are core abstraction in Spark"
    )

    val rdd = spark.sparkContext.parallelize(data)

    val toalWords = rdd.flatMap(line => line.split(" ")).count()

    println(s"The total number of words is : $toalWords")

    spark.stop()

  }
}



