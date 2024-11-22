import org.apache.spark.sql.SparkSession

object JoinRdds {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("JoinRdds")
      .master("local[1]")
      .getOrCreate()

    val rddNames = spark.sparkContext.parallelize(Seq(
      (1, "Alice"),
      (2, "Bob"),
      (3, "Charlie"),
      (4, "David")
    ))

    val rddScores = spark.sparkContext.parallelize(Seq(
      (1, 85),
      (2, 90),
      (3, 78),
      (5, 88)
    ))

    val joinedRDD = rddNames.join(rddScores)
    val result = joinedRDD.map {
      case (id, (name, score)) => (id, name, score)
    }

    result.collect().foreach {
      case (id, name, score) =>
        println(s"id: $id, name: $name, score: $score")
    }

    spark.stop()

  }
}
