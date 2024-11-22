import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object NoOfWords {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("NoOfWords")
      .master("local[1]")  // Use local mode with 2 threads, adjust as needed
      .getOrCreate()

    val data = Seq(
      "Spark is fast and general-purpose",
      "It provides powerful data processing",
      "RDDs are core abstraction in Spark"
    )

  }
}



