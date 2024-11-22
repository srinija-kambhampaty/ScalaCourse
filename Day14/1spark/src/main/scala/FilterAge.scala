import org.apache.spark.sql.SparkSession

object FilterAge {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Filter Age")
      .master("local[1]")
      .getOrCreate()

    val data = Seq(
      "1,John,25",
      "2,Jane,17",
      "3,Paul,30",
      "4,Anna,15",
      "5,Tom,22"
    )

    val rdd = spark.sparkContext.parallelize(data)

    val parsedRDD = rdd.map { row =>
      val columns = row.split(",")
      val id = columns(0).toInt
      val name = columns(1)
      val age = columns(2).toInt
      (id, name, age)
    }

    val filteredRDD = parsedRDD.filter { case (_, _, age) => age <= 18 }

    filteredRDD.collect().foreach {
      case (id, name, age) =>
        println(s"ID: $id, Name: $name, Age: $age")
    }
    spark.stop()
  }
}
