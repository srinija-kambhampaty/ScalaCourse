import org.apache.spark.sql.SparkSession

object AvgScore {
  def main(args: Array[String]): Unit = {
    // Initialize Spark session
    val spark = SparkSession.builder
      .appName("AverageScore")
      .master("local[1]")
      .getOrCreate()

    val data = Seq(
      (1, 85),
      (2, 90),
      (3, 78),
      (4, 92),
      (5, 88)
    )
    val rdd = spark.sparkContext.parallelize(data)
    val scoreAndCount = rdd.map { case (id, score) => (score, 1) }
    val (totalScore, totalCount) = scoreAndCount.reduce((a, b) => (a._1 + b._1, a._2 + b._2))
    val averageScore = totalScore.toDouble / totalCount
    println(s"Total Score: $totalScore, Total Records: $totalCount")
    println(s"Average Score: $averageScore")

    spark.stop()
  }
}
