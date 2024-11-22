import org.apache.spark.sql.SparkSession

object GroupByKeyAndSum {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Group by Key and Sum Values")
      .master("local[1]")
      .getOrCreate()

    val data = Seq(
      ("apple", 3),
      ("banana", 2),
      ("apple", 4),
      ("orange", 5),
      ("banana", 1),
      ("apple", 2)
    )

    val rdd = spark.sparkContext.parallelize(data)
    val sumByKeyRDD = rdd
      .reduceByKey(_ + _)

    sumByKeyRDD.collect().foreach {
      case (key, sum) =>
        println(s"Key: $key, Sum: $sum")
    }

    spark.stop()
  }
}
